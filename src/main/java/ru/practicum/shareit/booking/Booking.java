package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class Booking {
    Long id;
    String name;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    String status;

}
