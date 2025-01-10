package ru.practicum.admin.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.common.dto.user.CreateUserDto;
import ru.practicum.common.dto.user.UserDto;
import ru.practicum.common.exceptions.DuplicationException;
import ru.practicum.common.mapper.UserMapper;
import ru.practicum.common.model.User;
import ru.practicum.common.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public AdminUserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        log.debug("Attempting to create user. Name={}, email={}", createUserDto.getName(), createUserDto.getEmail());
        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            log.warn("User with email {} already exists", createUserDto.getEmail());
            throw new DuplicationException("User with email " + createUserDto.getEmail() + " already exists");
        }
        User user = userMapper.toEntity(createUserDto);
        User savedUser = userRepository.save(user);

        log.debug("User successfully created: name={}, email={}",
                savedUser.getName(),
                savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Attempting to delete user. Id={}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            log.warn("User with id {} does not exists", userId);
            throw new EntityNotFoundException("User with id " + userId + " does not exists");
        } else {
            User user = userOptional.get();
            userRepository.delete(user);

            log.debug("User successfully deleted: name={}, email={}",
                    user.getName(),
                    user.getEmail());
        }
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.debug("Attempting to fetch users. Ids={}, from={}, size={}", ids, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        List<User> users = (ids != null && !ids.isEmpty())
                ? userRepository.findByIdIn(ids, pageable)
                : userRepository.findAll(pageable).getContent();

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}