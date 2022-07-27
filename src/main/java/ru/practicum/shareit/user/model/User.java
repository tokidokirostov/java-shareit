package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class User {
    Long id;
    String email;
    String name;
}
