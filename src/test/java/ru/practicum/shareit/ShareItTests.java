package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ShareItTests {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    UserController userController;

    @Test
    public void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void user() throws Exception {
        User user = new User(null, "mmm@mail.ru", "mmm");
        User user1 = new User(null, "mmm@mail.ru", "nnn");
        User user2 = new User(null, "mmmmail.ru", "mmm");
        UserDto updateUser = new UserDto(null, "rrr@mail.ru", null);
        String jacksonUser1 = mapper.writeValueAsString(user1);
        String jacksonUser2 = mapper.writeValueAsString(user2);
        String jacksonUser3 = mapper.writeValueAsString(updateUser);

        String jacksonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(jacksonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("mmm"));
        mockMvc.perform(get("/users/1", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("mmm"));
        mockMvc.perform(post("/users").content(jacksonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/users").content(jacksonUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(patch("/users/1")
                        .content(jacksonUser3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/1", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("rrr@mail.ru"));

    }

    @Test
    public void items() throws Exception {
        User user = new User(null, "mmm@mail.ru", "mmm");
        String jacksonUser1 = mapper.writeValueAsString(user);
        ItemDto itemDto = new ItemDto(null, "name", "descr", true, null, null);
        ItemDto itemDto3 = new ItemDto(null, "name", "descr", null, null, null);
        ItemDto itemDto1 = new ItemDto(null, "", "описание", true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "name", "", true, null, null);
        String jacksonItem = mapper.writeValueAsString(itemDto);
        String jacksonItem1 = mapper.writeValueAsString(itemDto1);
        String jacksonItem2 = mapper.writeValueAsString(itemDto2);
        String jacksonItem3 = mapper.writeValueAsString(itemDto3);
        mockMvc.perform(post("/users").content(jacksonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mmm"));
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "1")
                        .content(jacksonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("name"));

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 4))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name"));
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "4")
                        .content(jacksonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "1")
                        .content(jacksonItem1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "1")
                        .content(jacksonItem2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "1")
                        .content(jacksonItem3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(patch("/items/1").header("X-Sharer-User-Id", "1")
                        .content(jacksonItem1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/items/search?text=опис")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].description").value("описание"));
    }

}
