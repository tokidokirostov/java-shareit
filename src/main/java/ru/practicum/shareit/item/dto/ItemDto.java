package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long request;
}
