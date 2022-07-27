package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserStorage storage;

    //Получение всех пользователей
    @Override
    public List<UserDto> getAllUsers() {
        log.info(storage.getAllUsers().toString());
        return storage.getAllUsers().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    //Добавление пользователя в базу
    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(storage.addUser(UserMapper.toUser(userDto)));
    }

    //Обновление полей пользователя
    @Override
    public UserDto patchUser(Long id, UserDto userDto) {
        return UserMapper.toUserDto(storage.patchUser(id, UserMapper.toUser(userDto)));
    }

    //Получение пользователя по id
    @Override
    public UserDto getUserById(Long id) {
        if (storage.getUserById(id) != null) {
            return UserMapper.toUserDto(storage.getUserById(id));
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
