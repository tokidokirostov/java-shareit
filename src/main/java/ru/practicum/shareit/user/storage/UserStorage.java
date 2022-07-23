package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();
    User addUser(User user);
    User patchUser(Long id, UserDto userDto);
    User getUserById(Long id);
    void delereUserById(Long id);
}
