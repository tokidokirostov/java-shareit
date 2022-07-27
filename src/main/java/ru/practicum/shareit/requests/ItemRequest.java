package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;

    public ItemRequest() {

    }
}
