package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    @Autowired
    UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен запрос GET /users");
        return userClient.getUsers();
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Получен запрос POST /users user - {}", userCreateDto.toString());
        return userClient.createUser(userCreateDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> patchUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users userDTO - {}", userDto.toString());
        return userClient.patchUser(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        return userClient.deleteUser(userId);
    }

}
