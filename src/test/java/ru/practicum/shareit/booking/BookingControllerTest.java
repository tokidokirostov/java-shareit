package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingServiceImp bookingService;

    User user = new User(1L, "user@user.com", "user");
    User user1 = new User(2L, "user1@user.com", "user1");
    Item itemTrue = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    BookingDto bookingDto = new BookingDto(1L, null, null, 1L, 1L, BookingStatus.APPROVED);

    BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22),
            LocalDateTime.of(2017, Month.JULY, 9, 11, 7, 22), 1L, 1L, BookingStatus.WAITING);

    BookingCreateDto bookingCreate1Dto = new BookingCreateDto(1L, LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22),
            null, 1L, 1L, BookingStatus.WAITING);

    Booking booking = new Booking(1L, LocalDateTime.of(2023, Month.JULY, 9, 11, 6, 22),
            LocalDateTime.of(2023, Month.JULY, 10, 11, 6, 22), itemTrue, user, BookingStatus.APPROVED);

    BookingStateDto bookingStateDto = BookingMapper.toBookingStateDto(booking);

    @Test
    void whenTryUsePostMappingBookin_thenReturnItemBookingDto() throws Exception {
        String jackson = mapper.writeValueAsString(bookingCreateDto);
        when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.itemId").value("1"));
    }

    @Test
    void whenTryUsePostMappingBookingWithOtherUser_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(bookingCreateDto);
        when(bookingService.addBooking(anyLong(), any())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUsePostMappingBookingWithOtherError_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(bookingCreateDto);
        when(bookingService.addBooking(anyLong(), any())).thenThrow(new ValidationException("Вещь недоступна"));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenTryUsePostMappingBookingWithEmptyEndTime_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(bookingCreate1Dto);
        when(bookingService.addBooking(anyLong(), any())).thenThrow(new ValidationException("Вещь недоступна"));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                //.andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenTryUsePatchMappingBookingWithOtherUser_thenReturnCustomException() throws Exception {
        when(bookingService.setApprove(anyLong(), anyLong(), anyBoolean())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUsePatchMappingBookingWithOtherError_thenReturnCustomException() throws Exception {
        when(bookingService.setApprove(anyLong(), anyLong(), anyBoolean())).thenThrow(new ValidationException("Статус уже подтвержден"));
        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenTryUseGetMappingBookingWithOtherUser_thenReturnCustomException() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetMappingBooking_thenReturnBookinStateDto() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingStateDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void whenTryUseGetAllMappingBookingWithOtherUser_thenReturnCustomException() throws Exception {
        when(bookingService.getAllBooking(anyLong(), anyString(), anyLong(), anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(get("/bookings?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetAllMappingBooking_thenReturnCustomException() throws Exception {
        when(bookingService.getAllBooking(anyLong(), anyString(), anyLong(), anyInt()))
                .thenThrow(new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
        mvc.perform(get("/bookings?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenTryUseGetAllMappingBooking_thenReturnListBookinStateDto() throws Exception {
        when(bookingService.getAllBooking(anyLong(), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(bookingStateDto));
        mvc.perform(get("/bookings?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("[0].id").value("1"));
    }

    @Test
    void whenTryUseGetAllMappingBookingByOwnerWithOtherUser_thenReturnCustomException() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), anyString(), anyLong(), anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(get("/bookings/owner?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetAllMappingBookingByOwner_thenReturnCustomException() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), anyString(), anyLong(), anyInt()))
                .thenThrow(new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
        mvc.perform(get("/bookings/owner?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenTryUseGetAllMappingBookingByOwner_thenReturnListBookinStateDto() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner?state=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("[0].id").value("1"));
    }
}
