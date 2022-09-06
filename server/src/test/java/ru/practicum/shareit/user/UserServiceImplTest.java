package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DublEmail;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user = new User(1L, "user@user.com", "user");
    UserDto userUpdateEmail = new UserDto(1L, "update_user@user.com", "user");
    UserDto userUpdateName = new UserDto(1L, "user@user.com", "update_user");
    UserDto userDto = UserMapper.toUserDto(user);
    UserDto userUpdateEmailDto = new UserDto(null, "update_user@user.com", null);
    UserDto userUpdateNameDto = new UserDto(null, null, "update_user");
    UserDto userUpdateEmptyDto = new UserDto(null, null, null);


    final Long userId = 1L;
    final String updateName = "update_user";
    final String updateEmail = "update_user@user.com";

    @Test
    void whenTryCreateUser_thenReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        var result = userService.addUser(userDto);
        assertEquals(userDto, result);
    }

    @Test
    void whenTryGetAllUsers_thenReturnListUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        var result = userService.getAllUsers();
        verify(userRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void whenTryGetUserById_thenReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        var result = userService.getUserById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertEquals(userDto, result);
    }

    @Test
    void whenTryGetUserByOtherId_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(anyLong()));
    }

    @Test
    void whenTryUpdateUserDoesNotExist_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.patchUser(anyLong(), userUpdateNameDto));
    }

    @Test
    void whenTryUpdateUserNewName_thenReturnUserIsUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = userService.patchUser(userId, userUpdateNameDto);
        assertEquals(updateName, result.getName());
        assertEquals(userUpdateName, result);
    }

    @Test
    void whenTryUpdateUserNoChange_thenReturnUserIsNotUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = userService.patchUser(userId, userUpdateEmptyDto);
        assertEquals(userDto, result);
    }

    @Test
    void whenTryUpdateUserNewEmail_thenReturnUserWithUpdateEmail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        var result = userService.patchUser(userId, userUpdateEmailDto);
        assertEquals(updateEmail, result.getEmail());
        assertEquals(userUpdateEmail, result);
    }

    @Test
    void whenTryUpdateUserDuplicateEmail_thenReturnCustomException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        assertThrows(DublEmail.class, () -> userService.patchUser(userId, userDto));
    }

    @Test
    void whenTryDeleteUserDoesNotExist_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(anyLong()));
    }

    @Test
    void whenTryDeleteUser_thenCallUserRepository() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        userService.deleteUserById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}

