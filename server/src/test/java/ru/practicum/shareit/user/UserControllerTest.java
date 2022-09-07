package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DublEmail;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserServiceImpl userService;

    private UserDto userDto = new UserDto(1L, "user@user.com", "user");
    private UserDto userDtoNoEmail = new UserDto(1L, null, "user");

    @Test
    void whenTryUseGetMappingUsers_thenReturnListUserDto() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("user"));
    }

    @Test
    void whenTryUseGetMappingUsersWithParam_thenReturnUserDto() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        mvc.perform(get("/users/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void whenTryUseGetMappingUsersWithParamAndOtherUser_thenReturnCustomException() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        mvc.perform(get("/users/6"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTryUsePostMappingUsers_thenReturnUserDto() throws Exception {
        String jacksonUser1 = mapper.writeValueAsString(userDto);
        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonUser1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void whenTryUsePatchMappingUsers_thenReturnUserDto() throws Exception {
        String jacksonUser1 = mapper.writeValueAsString(userDto);
        when(userService.patchUser(anyLong(), any())).thenReturn(userDto);
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonUser1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void whenTryUsePatchMappingUsersDoubleEmail_thenReturnCustomException() throws Exception {
        String jacksonUser1 = mapper.writeValueAsString(userDtoNoEmail);
        when(userService.patchUser(any(), any())).thenThrow(new DublEmail("Email in base"));
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonUser1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTryDeleteUser_thenRetursOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());
        verify(userService, times(1)).deleteUserById(anyLong());
    }
}
