package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemStorage;
    @Autowired
    private final UserRepository userStorage;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final BookingService bookingService;
    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    private final int page1 = 0;
    private final int size = 10;


    //Запрос всех вещей пользователя
    @Override
    public List<ItemBookingDto> getAllItems(Long userId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return itemStorage.findAllByOwnerIdOrderById(userId)
                .stream()
                .map(item -> ItemMapper.toItemTimesDto(item,
                        BookingMapper.toBookingForItemDto(bookingService.getLastBooking(item.getId(), localDateTime)),
                        BookingMapper.toBookingForItemDto(bookingService.getNextBooking(item.getId(), localDateTime)),
                        commentRepository.findByItemId(item.getId())
                                .stream()
                                .map(comment -> CommentMapper.toCommentDto(comment))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    //Запрос вещи
    @Override
    public ItemBookingDto getItem(Long userId, Long id) {
        if (itemStorage.findById(id).isEmpty()) {
            log.info("Item not found");
            throw new NotFoundException("Item not found");
        } else {
            List<CommentDto> comments = commentRepository.findByItemId(id)
                    .stream()
                    .map(comment -> CommentMapper.toCommentDto(comment))
                    .collect(Collectors.toList());
            LocalDateTime localDateTime = LocalDateTime.now();
            Item item = itemStorage.findById(id).get();
            if (item.getOwner().getId().equals(userId)) {
                return ItemMapper.toItemTimesDto(item,
                        BookingMapper.toBookingForItemDto(bookingService.getLastBooking(id, localDateTime)),
                        BookingMapper.toBookingForItemDto(bookingService.getNextBooking(id, localDateTime)),
                        comments);
            } else {
                return ItemMapper.toItemTimesDto(item, null, null, comments);
            }
        }
    }

    //Добавление вещи
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (userStorage.findById(userId).isPresent()) {
            log.info("Пользователь существует. Создание item.");
            ItemRequest itemRequest;
            if (itemDto.getRequestId() == null) {
                itemRequest = null;
            } else {
                itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).get();
            }
            Item item = ItemMapper.toItem(itemDto, userStorage.findById(userId).get(), itemRequest);
            return ItemMapper.toItemDto(itemStorage.save(item));
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    //Изменение вещи
    @Override
    public ItemDto patchUser(Long userId, Long id, ItemDto itemDto) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        } else {
            Optional<Item> patchItem = itemStorage.findById(id);
            log.info("Пользователь существует. Обновление item. ID - {}", id);
            if (patchItem.isEmpty() || !patchItem.get().getOwner().getId().equals(userId)) {
                throw new NotFoundException("Item not found");
            } else {
                if (patchItem.get().getId().equals(id)) {
                    if (itemDto.getName() != null) {
                        patchItem.get().setName(itemDto.getName());
                    }
                    if (itemDto.getDescription() != null) {
                        patchItem.get().setDescription(itemDto.getDescription());
                    }
                    if (itemDto.getAvailable() != null) {
                        patchItem.get().setIsAvailable(itemDto.getAvailable());
                    }
                }
                return ItemMapper.toItemDto(itemStorage.save(patchItem.get()));
            }
        }
    }

    //Поиск вещи
    @Override
    public List<ItemDto> searchItems(Optional<String> text) {
        Sort sort = Sort.unsorted();
        Pageable page = PageRequest.of(page1, size, sort);
        if (text.isEmpty() || text.get().equals(""))
            return new ArrayList<>();
        else
            return itemStorage.findItemByDescriptionContainingIgnoreCaseAndIsAvailableTrueOrNameContainingIgnoreCaseAndIsAvailableTrue(text.get(), text.get(), page)
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList());
    }

    @Override
    public CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) {
        if (userStorage.findById(userId).isEmpty()) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, localDateTime)
                    != null) {
                Item item = itemStorage.findById(itemId).get();
                User user = userStorage.findById(userId).get();
                commentDto.setCreated(localDateTime);
                return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
            } else {
                throw new ValidationException("Коментарий не сохранен");
            }
        }
    }


}
