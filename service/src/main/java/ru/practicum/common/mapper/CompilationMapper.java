package ru.practicum.common.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.dto.compilation.CreateCompilationDto;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {
    final private EventMapper eventMapper;

    @Autowired
    public CompilationMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public CompilationDto toDto(Compilation compilation) {
        return compilation == null ? null : CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(compilation.getEvents().stream()
                        .map(event -> eventMapper.toShortDto(event, event.getViews()))
                        .collect(Collectors.toList()))
                .build();
    }

    public Compilation toEntity(CreateCompilationDto createCompilationDto, List<Event> events) {
        return createCompilationDto == null ? null : Compilation.builder()
                .title(createCompilationDto.getTitle())
                .pinned(createCompilationDto.getPinned())
                .events(events == null ? new ArrayList<>() : events)
                .build();
    }

}