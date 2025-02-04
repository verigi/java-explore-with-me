package ru.practicum.general.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.general.dto.user.CreateUserDto;
import ru.practicum.general.dto.user.UpdateUserDto;
import ru.practicum.general.dto.user.UserDto;
import ru.practicum.general.dto.user.UserShortDto;
import ru.practicum.general.model.User;

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