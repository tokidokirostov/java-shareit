package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemCreateDto {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @NonNull
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    User owner;
    Long request;
}
