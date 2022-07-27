package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User user);

    User patchUser(Long id, User user);

    User getUserById(Long id);

    void delereUserById(Long id);
}
