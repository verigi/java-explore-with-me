package ru.practicum.general.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    @NotBlank(message = "User name must not be empty or null")
    @Length(min = 2, max = 250)
    private String name;
    @Email(message = "Invalid email format")
    @Length(min = 6, max = 254)
    private String email;
}