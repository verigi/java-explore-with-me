package ru.practicum.general.dto.compilation;

import lombok.*;
import ru.practicum.general.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}