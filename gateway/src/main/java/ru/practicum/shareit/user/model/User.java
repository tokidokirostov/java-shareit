package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    Long id;

    String email;

    String name;
}
