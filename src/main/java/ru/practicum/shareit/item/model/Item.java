package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class Item {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @NonNull
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    String owner;
    String request;

    public Item() {
    }
}
