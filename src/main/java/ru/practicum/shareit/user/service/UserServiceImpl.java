package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DublEmail;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository storage;

    //Получение всех пользователей
    @Override
    public List<UserDto> getAllUsers() {
        return storage.findAll().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    //Добавление пользователя в базу
    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(storage.save(UserMapper.toUser(userDto)));
    }

    //Обновление полей пользователя
    @Override
    public UserDto patchUser(Long id, UserDto userDto) {
        Optional<User> user = storage.findById(id);
        if (user.isPresent()) {
            if (userDto.getName() != null) {
                user.get().setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                if (storage.findByEmail(userDto.getEmail()).isEmpty()) {
                    user.get().setEmail(userDto.getEmail());
                } else {
                    log.info("Email in base");
                    throw new DublEmail("Email in base");
                }
            }
            return UserMapper.toUserDto(storage.save(user.get()));
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }

    }

    //Получение пользователя по id
    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(storage.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден!")));
    }

    //Удаление пользователя по id
    @Override
    public void deleteUserById(Long id) {
        if (storage.findById(id).isPresent()) {
            storage.deleteById(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
