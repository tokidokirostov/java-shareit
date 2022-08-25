package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImp;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImpTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImp bookingServiceImp;
    final Long USER_ID = 1L;
    final Long USER_ID2 = 2L;

    User user = new User(1L, "user@user.com", "user");
    User user1 = new User(2L, "user1@user.com", "user1");
    Item itemFalse = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", false, user, null);
    Item itemTrue = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    BookingDto bookingDto = new BookingDto(1L, null, null, 1L, 1L, BookingStatus.APPROVED);
    BookingDto bookingDtoBefore = new BookingDto(1L, LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22),
            null, 1L, 1L, BookingStatus.APPROVED);
    BookingDto bookingDtoBefore1 = new BookingDto(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22), 1L, 1L, BookingStatus.APPROVED);
    BookingDto bookingDtoBefore2 = new BookingDto(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 8, 11, 6, 22), 1L, 1L, BookingStatus.APPROVED);

    BookingDto bookingDtoBeforeOk1 = new BookingDto(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), 1L, 2L, BookingStatus.APPROVED);

    Booking booking = new Booking(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), itemTrue, user, BookingStatus.APPROVED);

    Booking booking1 = new Booking(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), itemTrue, user1, BookingStatus.APPROVED);

    Booking bookingWaiting = new Booking(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), itemTrue, user, BookingStatus.REJECTED);

    BookingDto bookingDtoIsOk = BookingMapper.toBookingDto(booking);
    BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22)
            , LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), 1L, null, null);

    BookingStateDto bookingStateDtoTru = BookingMapper.toBookingStateDto(booking);
    BookingStateDto bookingStateDtoFalse = BookingMapper.toBookingStateDto(bookingWaiting);


    @Test
    void whenTryCreateBookingWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDto));
    }

    @Test
    void whenTryCreateBookingWithOtherItem_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDto));
    }

    @Test
    void whenTryCreateBookingWithItemStartTimeBefore_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTrue));
        assertThrows(ValidationException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDtoBefore));
    }

    @Test
    void whenTryCreateBookingWithItemIsUnavailable_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemFalse));
        assertThrows(ValidationException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDtoBefore));
    }

    @Test
    void whenTryCreateBookingWithItemEndTimeBefore_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTrue));
        assertThrows(ValidationException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDtoBefore1));
    }

    @Test
    void whenTryCreateBookingWithItemStartTimeAfterEndTime_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTrue));
        assertThrows(ValidationException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDtoBefore2));
    }

    @Test
    void whenTryCreateBookingWithItemUserIdIqualsBookinUserId_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTrue));
        assertThrows(NotFoundException.class, () -> bookingServiceImp.addBooking(USER_ID, bookingDtoBeforeOk1));
    }

    @Test
    void whenTryCreateBooking_thenReturnBookingDto() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTrue));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        var result = bookingServiceImp.addBooking(2L, bookingDto1);
        assertNotNull(result);
        assertEquals(bookingDtoIsOk, result);
    }

    @Test
    void whenTrySetApproveBookingWithOtherUser_thenReturnCustomException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.setApprove(USER_ID, USER_ID, true));
    }

    @Test
    void whenTrySetApproveBookingWithOtherItem_thenReturnCustomException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemFalse));
        assertThrows(NotFoundException.class, () -> bookingServiceImp.setApprove(USER_ID2, USER_ID, true));
    }

    @Test
    void whenTrySetApproveBookingWithStatusApproved_thenReturnCustomException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemTrue));
        assertThrows(ValidationException.class, () -> bookingServiceImp.setApprove(USER_ID, USER_ID, true));
    }

    @Test
    void whenTrySetApproveBookingIsApproved_thenReturnCustomException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemTrue));
        when(bookingRepository.save(any())).thenReturn(booking);
        var result = bookingServiceImp.setApprove(USER_ID, USER_ID, true);
        assertNotNull(result);
        assertEquals(bookingStateDtoTru, result);
    }

    @Test
    void whenTrySetApproveBookingIsRejected_thenReturnCustomException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemTrue));
        when(bookingRepository.save(any())).thenReturn(booking);
        var result = bookingServiceImp.setApprove(USER_ID, USER_ID, false);
        assertNotNull(result);
        assertEquals(bookingStateDtoFalse, result);
    }

    @Test
    void whenTryGetBookingWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getBooking(USER_ID, USER_ID));
    }

    @Test
    void whenTryGetBookingWithOtherBooking_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getBooking(USER_ID, USER_ID));
    }

    @Test
    void whenTryGetBookingWithUserItemNotFound_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getBooking(2L, USER_ID));
    }

    @Test
    void whenTryGetBookingWithUserItemAuthor_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getBooking(3L, USER_ID));
    }

    @Test
    void whenTryGetBooking_thenReturnBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        var result = bookingServiceImp.getBooking(USER_ID, USER_ID);
        assertEquals(bookingStateDtoTru, result);
    }

    @Test
    void whenTryGetAllBookingWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getAllBooking(USER_ID, "ALL", USER_ID2, 2));
    }

    @Test
    void whenTryGetAllBookingWithStateAll_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "ALL", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStateFUTURE_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "FUTURE", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStatePAST_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "PAST", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStateCURRENT_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "CURRENT", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStateWAITING_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "WAITING", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStateREJECTED_thenReturnListBookingStateDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBooking(USER_ID, "REJECTED", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(bookingStateDtoTru, result.get(0));
    }

    @Test
    void whenTryGetAllBookingWithStateUNSUPPORTED_STATUS_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> bookingServiceImp.getAllBooking(USER_ID,
                "UNSUPPORTED_STATUS", USER_ID2, 2));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithOtherUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingServiceImp.getAllBookingByOwner(USER_ID, anyString(), USER_ID2, 2));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateALL_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInOrderByStartDesc(any(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "ALL", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateFUTURE_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(any(), any(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "FUTURE", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStatePAST_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "PAST", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateCURRENT_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "CURRENT", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateWAITING_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "WAITING", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateREJECTED_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        when(bookingRepository.findByItemIdInAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking)));
        var result = bookingServiceImp.getAllBookingByOwner(USER_ID, "REJECTED", USER_ID2, 2);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void whenTryGetAllBookingByOwnerWithStateUNSUPPORTED_STATUS_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemTrue));
        assertThrows(ValidationException.class, () -> bookingServiceImp.getAllBookingByOwner(USER_ID,
                "UNSUPPORTED_STATUS", USER_ID2, 2));
    }

    @Test
    void whenTryGetLastBooking_thenReturnBooking() {
        when(bookingRepository.findByItemIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking, booking1)));
        var result = bookingServiceImp.getLastBooking(USER_ID, LocalDateTime.now());
        assertEquals(booking, result);
    }

    @Test
    void whenTryGetNextBooking_thenReturnBooking() {
        when(bookingRepository.findByItemIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl(List.of(booking, booking1)));
        var result = bookingServiceImp.getNextBooking(USER_ID, LocalDateTime.now());
        assertEquals(booking, result);
    }

}
