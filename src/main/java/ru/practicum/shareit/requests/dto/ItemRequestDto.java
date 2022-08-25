package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class ItemRequestDto {
    Long id;
    @NotNull
    String description;
    Long requestor;
    LocalDateTime created;
    List<ItemDto> items;
}
