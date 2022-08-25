package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден - {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        if (itemRepository.findById(bookingDto.getItemId()).isEmpty()) {
            log.info("Вещь не найдена - {}", bookingDto.getItemId());
            throw new NotFoundException("Вещь не найдена");
        }
        if (!itemRepository.findById(bookingDto.getItemId()).get().getIsAvailable()) {
            log.info("Вещь недоступна");
            throw new ValidationException("Вещь недоступна");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            log.info("Не верная дата начала бронирования");
            throw new ValidationException("Не верная дата начала бронирования");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            log.info("Не верная дата конца бронирования");
            throw new ValidationException("Не верная дата конца бронирования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.info("дата конца раньше даты начала");
            throw new ValidationException("дата конца раньше даты начала");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (item.getOwner().getId().equals(userId)) {
            log.info("Пользователь не может забронировать свою вещь.");
            throw new NotFoundException("Пользователь не может забронировать свою вещь.");
        } else {
            User user = userRepository.findById(userId).get();
            bookingDto.setStatus(BookingStatus.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item)));
        }
    }

    @Override
    public BookingStateDto setApprove(Long userId, Long bookingId, boolean approved) {
        Long itemId = bookingRepository.findById(bookingId).get().getItem().getId();
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден - {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
            log.info("Пользователь не владелец вещи - {}", userId);
            throw new NotFoundException("Пользователь не владелец вещи");
        } else {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (approved) {
                if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                    log.info("Статус уже подтвержден");
                    throw new ValidationException("Статус уже подтвержден");
                }
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
        }
        return BookingMapper.toBookingStateDto(bookingRepository.findById(bookingId).get());
    }

    public BookingStateDto getBooking(Long userId, Long id) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден - {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<Booking> getBooking = bookingRepository.findById(id);
        if (getBooking.isEmpty()) {
            log.info("Booking не найден - {}", id);
            throw new NotFoundException("Booking не найден");
        }
        if (getBooking.get().getBooker().getId().equals(userId) ||
                getBooking.get().getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingStateDto(bookingRepository.findById(id).get());
        } else {
            log.info("Пользователь или не автор вещи, или не автор бронирования");
            throw new NotFoundException("Пользователь или не автор вещи, или не автор бронирования");
        }
    }

    @Override
    public List<BookingStateDto> getAllBooking(Long userId, String state, Long page1, Integer size) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден - {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Sort sort = Sort.unsorted();
        Pageable page = PageRequest.of(Math.toIntExact(page1), size, sort);
        Page<Booking> bookingPage;
        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookingPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page.previousOrFirst());
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                case FUTURE:
                    bookingPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page.previousOrFirst());
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                case PAST:
                    bookingPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page.previousOrFirst());
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                case CURRENT:
                    bookingPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page.previousOrFirst());
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                case WAITING:
                    bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page.previousOrFirst());
                        ;
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                case REJECTED:
                    bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page.previousOrFirst());
                        ;
                    }
                    return bookingPage.stream()
                            .map(booking -> BookingMapper.toBookingStateDto(booking))
                            .collect(Collectors.toList());
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS1");
            }
        } catch (Exception e) {
            log.info("Unknown state: " + state);
            throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> getAllBookingByOwner(Long userId, String state, Long page1, Integer size) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь не найден - {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Sort sort = Sort.unsorted();
        Pageable page = PageRequest.of(Math.toIntExact(page1), size, sort);
        List<Long> itemId = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        Page<Booking> bookingPage;
        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookingPage = bookingRepository.findByItemIdInOrderByStartDesc(itemId, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInOrderByStartDesc(itemId, page.previousOrFirst());
                    }
                    return bookingPage.toList();
                case FUTURE:
                    bookingPage = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInOrderByStartDesc(itemId, page.previousOrFirst());
                    }
                    return bookingPage.toList();
                case PAST:
                    bookingPage = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now(), page.previousOrFirst());
                    }
                    return bookingPage.toList();
                case CURRENT:
                    bookingPage = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemId,
                            LocalDateTime.now(), LocalDateTime.now(), page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemId,
                                LocalDateTime.now(), LocalDateTime.now(), page.previousOrFirst());
                    }
                    return bookingPage.toList();
                case WAITING:
                    bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.WAITING, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.WAITING, page.previousOrFirst());
                    }
                    return bookingPage.toList();
                case REJECTED:
                    bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.REJECTED, page);
                    while (bookingPage.isEmpty()) {
                        bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.REJECTED, page.previousOrFirst());
                    }
                    return bookingPage.toList();
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (Exception e) {
            log.info("Unknown state: " + state);
            throw new ValidationException("Unknown state: " + state);
        }
    }

    public Booking getLastBooking(Long id, LocalDateTime localDateTime) {
        return bookingRepository.findByItemIdAndEndBeforeOrderByStartDesc(id, localDateTime, Pageable.unpaged())
                .stream()
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    public Booking getNextBooking(Long id, LocalDateTime localDateTime) {
        return bookingRepository.findByItemIdAndStartAfterOrderByStartDesc(id, localDateTime, Pageable.unpaged())
                .stream()
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}