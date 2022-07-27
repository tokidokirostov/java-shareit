package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DublEmail;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    //Получение всех пользователей
    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }

    //Добавление пользователя в базу
    @Override
    public User addUser(User user) {
        if (!checkEmailInBase(user.getEmail())) {
            user.setId(++id);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new DublEmail("email существует в базе");
        }
    }

    //Обновление полей пользователя
    public User patchUser(Long id, User user) {
        if (!checkEmailInBase(user.getEmail())) {
            if (users.containsKey(id)) {
                if (user.getName() != null) {
                    users.get(id).setName(user.getName());
                }
                if (user.getEmail() != null) {
                    users.get(id).setEmail(user.getEmail());
                }
            }
        } else {
            throw new DublEmail("email существует в базе");
        }
        System.out.println(UserMapper.toUserDto(users.get(id)));
        return users.get(id);
    }

    //Получение пользователя по id
    public User getUserById(Long id) {
        return users.get(id);
    }

    //Удаление пользователя по id
    public void delereUserById(Long id) {
        users.remove(id);
    }

    private boolean checkEmailInBase(String email) {
        return users.values().stream()
                .anyMatch(a -> a.getEmail().equals(email));
    }

}
