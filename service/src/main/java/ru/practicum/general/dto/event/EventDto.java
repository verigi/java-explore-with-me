package ru.practicum.general.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.user.UserShortDto;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.model.Location;

import java.time.LocalDateTime;

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
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private StateEvent state;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private UserShortDto initiator;
    private CategoryDto category;
    private Location location;
    private int confirmedRequests;
    private int views;
}