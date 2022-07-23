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
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserStorage storage;

    //Получение всех пользователей
    @Override
    public List<User> getAllUsers() {
        log.info(storage.getAllUsers().toString());
        return storage.getAllUsers();
    }

    //Добавление пользователя в базу
    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    //Обновление полей пользователя
    @Override
    public User patchUser(Long id, UserDto userDto) {
        return storage.patchUser(id, userDto);
    }

    //Получение пользователя по id
    @Override
    public User getUserById(Long id) {
        if (storage.getUserById(id) != null) {
            return storage.getUserById(id);
        } else {
            throw new ValidationException("Пользователь не найден");
        }
    }

    //Удаление пользователя по id
    @Override
    public void deleteUserById(Long id) {
        if (storage.getUserById(id) != null) {
            storage.delereUserById(id);
        } else {
            throw new ValidationException("Пользователь не найден");
        }
    }

}
