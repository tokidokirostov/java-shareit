package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    //Запрос всех вещей пользователя
    @Override
    public List<ItemBookingDto> getAllItems(Long userId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<CommentDto> comments = new ArrayList<>();
        return itemStorage.findAllByOwnerId(userId)
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
        if (userStorage.findById(userId) != null) {
            log.info("Пользователь существует. Создание item.");
            Item item = ItemMapper.toItem(itemDto, userStorage.findById(userId).get());
            return ItemMapper.toItemDto(itemStorage.save(item));
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    //Изменение вещи
    @Override
    public ItemDto patchUser(Long userId, Long id, ItemDto itemDto) {
        if (userStorage.findById(userId) == null) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        } else {
            log.info("Пользователь существует. Обновление item. ID - {}", id);
            Item patchItem = itemStorage.findById(id).get();
            if (!patchItem.getOwner().getId().equals(userId) || patchItem == null) {
                throw new NotFoundException("Item not found");
            } else {
                if (patchItem.getId().equals(id)) {
                    if (itemDto.getName() != null) {
                        patchItem.setName(itemDto.getName());
                    }
                    if (itemDto.getDescription() != null) {
                        patchItem.setDescription(itemDto.getDescription());
                    }
                    if (itemDto.getAvailable() != null) {
                        patchItem.setIsAvailable(itemDto.getAvailable());
                    }
                }
                return ItemMapper.toItemDto(itemStorage.save(patchItem));
            }
        }
    }

    //Поиск вещи
    @Override
    public List<ItemDto> searchItems(Optional<String> text) {
        if (text.isEmpty() || text.get().equals(""))
            return new ArrayList<>();
        else
            return itemStorage.findItemByDescriptionContainingIgnoreCaseAndIsAvailableTrueOrNameContainingIgnoreCaseAndIsAvailableTrue(text.get(), text.get())
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList());
    }

    @Override
    public CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto) {
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
