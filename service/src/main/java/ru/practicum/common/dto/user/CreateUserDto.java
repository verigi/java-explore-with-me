package ru.practicum.common.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "User name must not be empty or null")
    @Size(min = 2, max = 250)
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 250)
    private String email;
}