package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class IntegrationUserTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @Test
    public void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void User() throws Exception {
        UserCreateDto userCreateDto1 = new UserCreateDto(null, "mmm@mail.ru", "mmm");
        UserCreateDto userCreateDto3 = new UserCreateDto(null, "mmmmail.ru", "mmm");
        UserDto updateUser = new UserDto(null, "rrr@mail.ru", null);
        String jacksonUser = mapper.writeValueAsString(userCreateDto1);
        String jacksonUser2 = mapper.writeValueAsString(userCreateDto3);
        String jacksonUser3 = mapper.writeValueAsString(updateUser);
        mockMvc.perform(post("/users").content(jacksonUser).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("mmm"));
        mockMvc.perform(get("/users/1", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("mmm"));
        mockMvc.perform(post("/users").content(jacksonUser2).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(patch("/users/1")
                        .content(jacksonUser3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("rrr@mail.ru"));
        mockMvc.perform(get("/users/1", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("rrr@mail.ru"));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}


