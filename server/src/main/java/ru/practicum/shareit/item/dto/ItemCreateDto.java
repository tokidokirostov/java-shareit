package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class ItemCreateDto {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    Long requestId;
}
