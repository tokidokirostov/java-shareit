package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoController;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDtoController userDtoController) {
        log.info("Получен запрос POST /users user - {}", userDtoController.toString());

        return userService.addUser(UserMapper.toUserDto(userDtoController));
    }

    @PatchMapping("{id}")
    public UserDto patchUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users userDTO - {}", userDto.toString());
        return userService.patchUser(id, userDto);
    }

    @DeleteMapping("{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Получен запрос DELETE /users/{}", id);
        userService.deleteUserById(id);
    }

}
