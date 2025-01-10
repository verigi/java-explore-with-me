package ru.practicum.common.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryDto {
    @NotBlank(message = "Category name must not be empty or null")
    @Size(min = 1, max = 50, message = "Name must be from 1 up to 50 symbols")
    private String name;
}