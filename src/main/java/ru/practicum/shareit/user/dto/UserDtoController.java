package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserDtoController {
    Long id;
    @NonNull
    @Email
    String email;
    @NonNull
    String name;
}
