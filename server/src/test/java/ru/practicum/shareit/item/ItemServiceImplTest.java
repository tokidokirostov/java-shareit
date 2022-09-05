package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;
    User user = new User(1L, "user@user.com", "user");
    User user1 = new User(2L, "other_user@user.com", "other_user");
    Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    ItemRequest itemRequest = new ItemRequest(1L, "Хотел бы воспользоваться щёткой для обуви", user1, null);
    Item itemWithRequest = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви",
            true, user, itemRequest);

    Item item1 = new Item(2L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    ItemDto itemDto = ItemMapper.toItemDto(item);
    ItemDto itemDtoRequest = new ItemDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
            true, 1L);
    Comment comment = new Comment(1L, "Add comment", item, user, null);
    ItemDto itemDtoNewName = new ItemDto(null, "Update", null, null, null);
    ItemDto itemDtoNewDesciption = new ItemDto(null, null, "Description update", null, null);
    ItemDto itemDtoNewAvailable = new ItemDto(null, null, null, false, null);

    ItemBookingDto itemBookingDto = new ItemBookingDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
            true, null, null, new ArrayList<>(), null);
    Booking lastBooking = new Booking(1L, null, null, item, user, BookingStatus.WAITING);
    BookingForItemDto last = BookingMapper.toBookingForItemDto(lastBooking);
    Booking nextBooking = new Booking(2L, null, null, item1, user1, BookingStatus.WAITING);
    BookingForItemDto next = BookingMapper.toBookingForItemDto(nextBooking);
    CommentDto commentDto = CommentMapper.toCommentDto(comment);
    CommentDto commentDto1 = new CommentDto(1L, "Add comment", "user", null);
    List<CommentDto> comments = List.of(commentDto);
    ItemBookingDto itemBookingDtoFull = new ItemBookingDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
            true, last, next, comments, null);


    final Long userId = 1L;
    final Long itemId = 1L;

    @Test
    void whenTryCreateItemWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addItem(anyLong(), itemDto));
    }

    @Test
    void whenTryCreateItemWithoutItemRequest_thenReturnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(Optional.of(item).get());
        var result = itemService.addItem(userId, itemDto);
        assertEquals(itemDto, result);
    }

    @Test
    void whenTryCreateItemWithItemRequest_thenReturnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(Optional.of(itemWithRequest).get());
        var result = itemService.addItem(userId, itemDtoRequest);
        assertEquals(itemDtoRequest, result);
    }

    @Test
    void whenTryUpdateItemDoesNotExistItem_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.patchUser(anyLong(), itemId, itemDto));
    }

    @Test
    void whenTryUpdateItemWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.patchUser(anyLong(), itemId, itemDto));
    }

    @Test
    void whenTryUpdateItemUserNotOwner_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        assertThrows(NotFoundException.class, () -> itemService.patchUser(anyLong(), itemId, itemDto));
    }

    @Test
    void whenTryUpdateItemWithNewName_thenReturnItemNameUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = itemService.patchUser(userId, itemId, itemDtoNewName);
        assertEquals(itemDtoNewName.getName(), result.getName());
    }

    @Test
    void whenTryUpdateItemWithNewDescription_thenReturnItemDescriptionUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = itemService.patchUser(userId, itemId, itemDtoNewDesciption);
        assertEquals(itemDtoNewDesciption.getDescription(), result.getDescription());
    }

    @Test
    void whenTryUpdateItemWithNewAvailable_thenReturnItemAvailableUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = itemService.patchUser(userId, itemId, itemDtoNewAvailable);
        assertEquals(itemDtoNewAvailable.getAvailable(), result.getAvailable());
    }

    @Test
    void whenTryGetItemDoesNotExistItem_thenReturnCustomException() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItem(anyLong(), itemId));
    }

    @Test
    void whenTryGetItemNoCommentNoOwner_thenReturnItemNoCommentNoBooking() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        var result = itemService.getItem(userId, itemId);
        assertEquals(itemBookingDto, result);
    }

    @Test
    void whenTryGetItemWithCommentWithBooking_thenReturnItemWithCommentWithBooking() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(bookingService.getLastBooking(anyLong(), any())).thenReturn(lastBooking);
        when(bookingService.getNextBooking(anyLong(), any())).thenReturn(nextBooking);
        var result = itemService.getItem(userId, itemId);
        assertEquals(itemBookingDtoFull, result);
    }

    @Test
    void whenTryGetAllItems_thenReturnListItems() {
        when(itemRepository.findAllByOwnerIdOrderById(anyLong())).thenReturn(List.of(item));
        when(bookingService.getLastBooking(anyLong(), any())).thenReturn(lastBooking);
        when(bookingService.getNextBooking(anyLong(), any())).thenReturn(nextBooking);
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        var result = itemService.getAllItems(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemBookingDtoFull, result.get(0));
    }

    @Test
    void whenTrySearchItemsWithTextOut_thenReturnEmptyList() {
        var result = itemService.searchItems(Optional.empty());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void whenTrySearchItemsWithTextEmpty_thenReturnEmptyList() {
        var result = itemService.searchItems(Optional.of(""));
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void whenTrySearchItemsWithText_thenReturnList() {
        when(itemRepository.findItemByDescriptionContainingIgnoreCaseAndIsAvailableTrueOrNameContainingIgnoreCaseAndIsAvailableTrue(anyString(), anyString(), any()))
                .thenReturn(new PageImpl(List.of(item)));
        var result = itemService.searchItems(Optional.of("hhh"));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
    }

    @Test
    void whenTrySaveCommentWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.saveComment(itemId, userId, any()));
    }

    @Test
    void whenTrySaveCommentWithoutItem_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(null);
        assertThrows(ValidationException.class, () -> itemService.saveComment(itemId, userId, commentDto));
    }

    @Test
    void whenTrySaveComment_thenReturnSavedComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(nextBooking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        var result = itemService.saveComment(itemId, userId, commentDto1);
        assertEquals(commentDto, result);
    }
}
