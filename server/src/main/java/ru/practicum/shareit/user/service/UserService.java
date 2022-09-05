package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    //Получение всех пользователей
    List<UserDto> getAllUsers();

    //Добавление пользователя в базу
    UserDto addUser(UserDto userDto);

    //Обновление полей пользователя
    UserDto patchUser(Long id, UserDto userDto);

    //Получение пользователя по id
    UserDto getUserById(Long id);

    //Удаление пользователя по id
    void deleteUserById(Long id);
}
