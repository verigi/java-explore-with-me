package ru.practicum.common.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.user.UserShortDto;
import ru.practicum.common.enums.EventState;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Location;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime published;
    private EventState state;
    private boolean paid;
    private int participantLimit;
    private boolean moderationRequest;
    private UserShortDto initiatorDto;
    private CategoryDto categoryDto;
    private Location location;
    private int confirmedRequests;
    private int views;
}