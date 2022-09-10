package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        if (userRepository.findById(userId).isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            itemRequestDto.setCreated(localDateTime);
            itemRequestDto.setItems(itemRepository.findByRequestIdList(itemRequestDto.getId())
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList()));
            return ItemRequestMapper.toItemRequestDto(
                    itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user.get())),
                    itemRepository.findByRequestIdList(itemRequestDto.getId())
                            .stream()
                            .map(item -> ItemMapper.toItemDto(item))
                            .collect(Collectors.toList()));
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    @Override
    public List<ItemRequestDto> getAllItemsRequest(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                    .stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                            itemRepository.findByRequestIdList(itemRequest.getId())
                                    .stream()
                                    .map(item -> ItemMapper.toItemDto(item))
                                    .collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
            for (ItemRequestDto itemRequestDto1 : itemRequestDtoList) {
                itemRequestDto1.setItems(itemRepository.findByRequestIdList(itemRequestDto1.getId())
                        .stream()
                        .map(item -> ItemMapper.toItemDto(item))
                        .collect(Collectors.toList()));
            }
            return itemRequestDtoList;
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    @Override
    public List<ItemRequestDto> getAllItemsByPage(Long userId, Long itemRequestId, Integer size) {
        Sort sort = Sort.by("created").descending();
        Pageable page = PageRequest.of(Math.toIntExact(itemRequestId), size, sort);
        return itemRequestRepository.findByRequestorIdIsNotOrderByCreatedDesc(userId, page)
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                        itemRepository.findByRequestIdList(itemRequest.getId())
                                .stream()
                                .map(item -> ItemMapper.toItemDto(item))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    //GET /requests/all?from={from}&size={size}

    @Override
    public ItemRequestDto getItemRequest(Long requestId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            log.info("Запрос не найден!");
            throw new NotFoundException("Запрос не найден!");
        } else {
            List<ItemDto> itemDtoList = itemRepository.findByRequestIdList(requestId)
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList());
            return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).get(), itemDtoList);
        }
    }

}
