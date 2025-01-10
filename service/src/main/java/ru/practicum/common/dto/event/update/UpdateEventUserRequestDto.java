package ru.practicum.common.dto.event.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.enums.ActionState;
import ru.practicum.common.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequestDto {
    private String title;
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private boolean paid;
    private int participantLimit;
    private boolean moderationRequest;
    private Long categoryId;
    private Location location;
    private ActionState actionState;
}