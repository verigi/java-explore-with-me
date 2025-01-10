package ru.practicum.common.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompilationDto {
    @NotBlank(message = "Title must not be empty or null")
    private String title;
    @NotNull(message = "Pinned status must not be null")
    private Boolean pinned;
    @NotNull(message = "Event ids must not be null")
    private List<Long> eventsId;
}