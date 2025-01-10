package ru.practicum.admin.service.user;


import ru.practicum.common.dto.user.CreateUserDto;
import ru.practicum.common.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {
    UserDto createUser(CreateUserDto createUserDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}