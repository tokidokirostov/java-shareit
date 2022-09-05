package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    final Long userId = 1L;
    User user = new User(1L, "user@user.com", "user");
    User user1 = new User(2L, "other_user@user.com", "other_user");
    Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    ItemDto itemDto = ItemMapper.toItemDto(item);
    ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви", user1, null);
    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, List.of(itemDto));
    ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "Хотел бы воспользоваться щёткой для обуви", 2L, null, List.of(itemDto));


    @Test
    void whenTryCreateItemRequestWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(userId, any()));
    }

    @Test
    void whenTryCreateItemRequest_thenReturnItemRequestSaved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByRequestIdList(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        var result = itemRequestService.addItemRequest(userId, itemRequestDto);
        assertNotNull(result);
        assertEquals(itemRequestDto1, result);
    }

    @Test
    void whenTryGetAllItemsRequestWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemsRequest(anyLong()));
    }

    @Test
    void whenTryGetAllItemsReques_thenReturnListItemRequestDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIdList(anyLong())).thenReturn(List.of(item));
        var result = itemRequestService.getAllItemsRequest(userId);
        assertEquals(1, result.size());
        assertEquals(List.of(itemRequestDto1), result);
    }

    @Test
    void whenTryGetAllItemsByPage_thenReturnListItemRequestDto() {
        when(itemRequestRepository.findByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any())).thenReturn(new PageImpl(List.of(itemRequest)));
        when(itemRepository.findByRequestIdList(anyLong())).thenReturn(List.of(item));
        var result = itemRequestService.getAllItemsByPage(anyLong(), anyLong(), 10);
        assertEquals(1, result.size());
        assertEquals(List.of(itemRequestDto1), result);
    }

    @Test
    void whenTryGetItemsRequestWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(userId, anyLong()));
    }

    @Test
    void whenTryGetItemsRequestWithOtherItemRequest_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(userId, anyLong()));
    }

    @Test
    void whenTryGetItemsRequest_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdList(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        var result = itemRequestService.getItemRequest(userId, anyLong());
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
    }
}
