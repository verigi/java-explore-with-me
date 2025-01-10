package ru.practicum.common.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.common.dto.user.CreateUserDto;
import ru.practicum.common.dto.user.UpdateUserDto;
import ru.practicum.common.dto.user.UserDto;
import ru.practicum.common.dto.user.UserShortDto;
import ru.practicum.common.model.User;

import java.time.LocalDateTime;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return user == null ? null : UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toShortDto(User user) {
        return user == null ? null : UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toEntity(CreateUserDto createUserDto) {
        return createUserDto == null ? null : User.builder()
                .name(createUserDto.getName())
                .email(createUserDto.getEmail())
                .build();
    }

    public User updateEntity(User user, UpdateUserDto updateUserDto) {
        if (user == null || updateUserDto == null) {
            return null;
        }
        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        user.setUpdated(LocalDateTime.now());
        return user;
    }
}