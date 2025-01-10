package ru.practicum.common.dto.event;

import lombok.*;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.user.UserShortDto;

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
    private UserShortDto initiatorDto;
    private CategoryDto categoryDto;
    private int views;
}