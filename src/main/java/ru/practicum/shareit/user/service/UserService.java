package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    //Получение всех пользователей
    List<User> getAllUsers();

    //Добавление пользователя в базу
    User addUser(User user);

    //Обновление полей пользователя
    User patchUser(Long id, UserDto userDto);

    //Получение пользователя по id
    User getUserById(Long id);

    //Удаление пользователя по id
    void deleteUserById(Long id);
}
