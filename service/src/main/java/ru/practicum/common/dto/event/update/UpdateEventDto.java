package ru.practicum.common.dto.event.update;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {
    @NotBlank(message = "Title must not be empty or null")
    @Size(min = 2, max = 50)
    private String title;
    @NotBlank(message = "Annotation must not be empty or null")
    @Size(min = 2, max = 250)
    private String annotation;
    @NotBlank(message = "Description must not be empty or null")
    @Size(min = 2, max = 500)
    private String description;
    @Future(message = "Event date must be in future")
    private LocalDateTime eventDate;
    private Boolean paid;
    @Min(value = 0, message = "Participant must be 0 or more")
    private Integer participantLimit;
    private Boolean moderationRequest;
    @NotNull(message = "Category ID must not be null")
    private Long categoryId;
    @NotNull(message = "Location must not be null")
    private Location location;
}