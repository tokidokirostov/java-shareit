package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl {
    @Autowired
    private final UserStorage storage;

    //Получение всех пользователей
    public List<User> getAllUsers() {
        log.info(storage.getAllUsers().toString());
        return storage.getAllUsers();
    }

    //Добавление пользователя в базу
    public User addUser(User user) {
        return storage.addUser(user);
    }

    //Обновление полей пользователя
    public User patchUser(Long id, UserDto userDto) {
        return storage.patchUser(id, userDto);
    }

    //Получение пользователя по id
    public User getUserById(Long id) {
        if (storage.getUserById(id) != null) {
            return storage.getUserById(id);
        } else {
            throw new ValidationException("Пользователь не найден");
        }
    }

    //Удаление пользователя по id
    public void deleteUserById(Long id) {
        if (storage.getUserById(id) != null) {
            storage.delereUserById(id);
        } else {
            throw new ValidationException("Пользователь не найден");
        }
    }

}
