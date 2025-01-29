package ru.practicum.general.dto.event;

import lombok.*;
import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private LocalDateTime eventDate;
    private boolean paid;
    private UserShortDto initiator;
    private CategoryDto category;
    private int confirmedRequests;
    private int views;
}