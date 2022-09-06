package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemServiceImpl itemService;

    User user = new User(1L, "user@user.com", "user");
    Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    ItemDto itemDto = ItemMapper.toItemDto(item);
    ItemBookingDto itemBookingDto = new ItemBookingDto(1L, "Щётка для обуви", "Стандартная щётка для обуви",
            true, null, null, new ArrayList<>(), null);
    Comment comment = new Comment(1L, "Add comment", item, user, null);
    CommentDto commentDto1 = new CommentDto(1L, "Add comment", "user", null);

    @Test
    void whenTryUseGetMappingAllItems_thenReturnItemBookingDto() throws Exception {
        when(itemService.getAllItems(anyLong())).thenReturn(List.of(itemBookingDto));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Щётка для обуви"));
    }

    @Test
    void whenTryUseGetMappingItems_thenReturnCustomException() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetMappingItems_thenReturnItemBookingDto() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemBookingDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Щётка для обуви"));
    }

    @Test
    void whenTryUsePostMappingItemsWithOtherUser_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(itemDto);
        when(itemService.addItem(anyLong(), any())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUsePostMappingItems_thenReturnItemDto() throws Exception {
        String jackson = mapper.writeValueAsString(itemDto);
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Щётка для обуви"));
    }

    @Test
    void whenTryUsePatchMappingItemsWithOtherUser_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(itemDto);
        when(itemService.patchUser(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUsePatchMappingItems_thenReturnItemDto() throws Exception {
        String jackson = mapper.writeValueAsString(itemDto);
        when(itemService.patchUser(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Щётка для обуви"));
    }

    @Test
    void whenTryUseGetMappingItemsSearch_thenReturnListItemBookingDto() throws Exception {
        when(itemService.searchItems(any())).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=для")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Щётка для обуви"));
    }

    @Test
    void whenTryUsePostMappingCommentsWithOtherUser_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(commentDto1);
        when(itemService.saveComment(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUsePostMappingComment_thenReturnCommentDto() throws Exception {
        String jackson = mapper.writeValueAsString(commentDto1);
        when(itemService.saveComment(anyLong(), anyLong(), any())).thenReturn(commentDto1);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.text").value("Add comment"));
    }
}
