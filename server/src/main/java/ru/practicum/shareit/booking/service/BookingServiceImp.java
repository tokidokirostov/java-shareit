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
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    private final String all = "ALL";
    private final String future = "FUTURE";
    private final String past = "PAST";
    private final String current = "CURRENT";
    private final String waiting = "WAITING";
    private final String rejected = "REJECTED";


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
        int allPage;
        switch (state) {
            case all:
                bookingPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                allPage = bookingPage.getTotalPages();
                System.out.println("total page - " + allPage);
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case future:
                bookingPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case past:
                bookingPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case current:
                bookingPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                System.out.println("1AllPage - " + allPage);
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case waiting:
                bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            case rejected:
                bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.stream()
                        .map(booking -> BookingMapper.toBookingStateDto(booking))
                        .collect(Collectors.toList());
            default:
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
        int allPage;
        List<Long> itemId = itemRepository.findAllByOwnerIdOrderById(userId)
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        Page<Booking> bookingPage;
        System.out.println("BookinState - " + state);
        switch (state) {
            case all:
                bookingPage = bookingRepository.findByItemIdInOrderByStartDesc(itemId, page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInOrderByStartDesc(itemId, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            case future:
                bookingPage = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            case past:
                bookingPage = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            case current:
                bookingPage = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                allPage = bookingPage.getTotalPages();
                System.out.println("AllPage - " + allPage);
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemId,
                            LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            case waiting:
                bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.WAITING, page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.WAITING, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            case rejected:
                bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.REJECTED, page);
                allPage = bookingPage.getTotalPages();
                if (page1 >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                        System.out.println("Page - " + page);
                        System.out.println("AllPage - " + allPage);
                    }
                    bookingPage = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemId, BookingStatus.REJECTED, PageRequest.of(allPage, size, sort));
                }
                return bookingPage.toList();
            default:
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
