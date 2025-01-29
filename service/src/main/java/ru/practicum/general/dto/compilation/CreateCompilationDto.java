package ru.practicum.general.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompilationDto {
    @NotBlank(message = "Title must not be empty or null")
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    @NotNull(message = "Event ids must not be null")
    private List<Long> events = new ArrayList<>();
}