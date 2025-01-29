package ru.practicum.general.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.general.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {
    @NotBlank(message = "Title must not be empty or null")
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank(message = "Annotation must not be empty or null")
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotBlank(message = "Description must not be empty or null")
    @Size(min = 20, max = 7000)
    private String description;
    @Future(message = "Event date must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Builder.Default
    private boolean paid = false;
    @Min(value = 0, message = "Participant must be 0 or more")
    private int participantLimit;
    @Builder.Default
    private Boolean requestModeration = true;
    private Long category;
    @NotNull(message = "Location must not be null")
    private Location location;
}