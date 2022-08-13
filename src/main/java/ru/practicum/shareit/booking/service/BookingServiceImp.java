package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImp implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        if (itemRepository.findById(bookingDto.getItemId()).isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!itemRepository.findById(bookingDto.getItemId()).get().getIsAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Не верная начала бронирования");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Не верная дата конца бронирования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("дата конца раньше даты начала");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не может забронировать свою вещь.");
        } else {
            User user = userRepository.findById(userId).get();
            bookingDto.setStatus(BookingStatus.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item)));
        }
    }

    @Override
    public Booking setApprove(Long userId, Long bookingId, boolean approved) {
        Long itemId = bookingRepository.findById(bookingId).get().getItem().getId();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не владелец вещи");
        } else {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (approved) {
                if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new ValidationException("Статус уже подтвержден");
                }
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
        }
        return bookingRepository.findById(bookingId).get();
    }

    public Booking getBooking(Long userId, Long id) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<Booking> getBooking = bookingRepository.findById(id);
        if (getBooking.isEmpty()) {
            throw new NotFoundException("Booking не найден");
        }
        if (getBooking.get().getBooker().getId().equals(userId) ||
                getBooking.get().getItem().getOwner().getId().equals(userId)) {
            return bookingRepository.findById(id).get();
        } else {
            throw new NotFoundException("Пользователь вещи или автор не найден");
        }
    }

    @Override
    public List<BookingStateDto> getAllBooking(Long userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> getAllBookingByOwner(Long userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Long> itemId = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        switch (state) {
            case "ALL":
                return bookingRepository.findByItemIdInOrderByStartDesc(itemId);
            case "FUTURE":
                return bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now());
            case "PAST":
                return bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now());
            case "CURRENT":
                return bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemId,
                        LocalDateTime.now(), LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.REJECTED);
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public Booking getLastBooking(Long id, LocalDateTime localDateTime) {
        return bookingRepository.findByItemIdAndEndBeforeOrderByStartDesc(id, localDateTime)
                .stream()
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    public Booking getNextBooking(Long id, LocalDateTime localDateTime) {
        return bookingRepository.findByItemIdAndStartAfterOrderByStartDesc(id, localDateTime)
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
