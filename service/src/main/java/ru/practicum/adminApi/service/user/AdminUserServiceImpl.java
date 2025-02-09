package ru.practicum.adminApi.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.user.CreateUserDto;
import ru.practicum.general.dto.user.UserDto;
import ru.practicum.general.mapper.UserMapper;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityHandler entityHandler;

    @Autowired
    public AdminUserServiceImpl(UserRepository userRepository, UserMapper userMapper, EntityHandler entityHandler) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.entityHandler = entityHandler;
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        log.debug("Attempting to create user. Name={}, email={}", createUserDto.getName(), createUserDto.getEmail());
        entityHandler.validateEmailUniqueness(createUserDto.getEmail());

        User user = userMapper.toEntity(createUserDto);
        User savedUser = userRepository.save(user);

        log.debug("User created. Id={}",
                savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Attempting to delete user. Id={}", userId);
        User user = entityHandler.findEntityById(userRepository, userId, "User");

        userRepository.delete(user);

        log.debug("User deleted. Id={}",
                userId);

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.debug("Attempting to fetch users. Ids={}, from={}, size={}", ids, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        List<User> users = (ids != null && !ids.isEmpty())
                ? userRepository.findByIdIn(ids, pageable)
                : userRepository.findAll(pageable).getContent();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        if (userDtos.isEmpty()) {
            log.debug("No users found");
        } else {
            log.debug("Users fetched. Size={}", userDtos.size());
        }
        return userDtos;
    }
}