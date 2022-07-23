package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class User {
    Long id;
    @NonNull
    @Email
    String email;
    @NonNull
    String name;
}
