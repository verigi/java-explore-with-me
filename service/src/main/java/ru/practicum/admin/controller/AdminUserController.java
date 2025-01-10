package ru.practicum.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.user.AdminUserService;
import ru.practicum.common.dto.user.CreateUserDto;
import ru.practicum.common.dto.user.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final AdminUserService service;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.debug("New POST request received. User name: \"{}\", user email: \"{}\"", createUserDto.getName(), createUserDto.getEmail());
        UserDto userDto = service.createUser(createUserDto);
        log.debug("User \"{}\" successfully saved", createUserDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.debug("New DELETE request received. User id: {}", userId);
        service.deleteUser(userId);
        log.debug("User with id: \"{}\" successfully deleted", userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) List<Long> ids,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Params: ids={}, from={}, size={}", ids, from, size);
        List<UserDto> userDtos = service.getUsers(ids, from, size);
        //ex 400 BAD_REQUEST
        if (userDtos.isEmpty()) {
            log.debug("No users fetched. Return empty list");
        } else {
            log.debug("Users successfully fetched. Count: {}", userDtos.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }
}