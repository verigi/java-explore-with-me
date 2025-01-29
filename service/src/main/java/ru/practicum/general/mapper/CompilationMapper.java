package ru.practicum.general.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.general.dto.compilation.CreateCompilationDto;
import ru.practicum.general.dto.compilation.UpdateCompilationDto;
import ru.practicum.general.model.Compilation;
import ru.practicum.general.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {
    private final EventMapper eventMapper;

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
                .pinned(createCompilationDto.getPinned() == null ? Boolean.FALSE : createCompilationDto.getPinned())
                .events(events == null || events.isEmpty() ? new ArrayList<>() : events)
                .build();
    }

    public Compilation updateEntity(Compilation compilation, UpdateCompilationDto dto) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        return compilation;
    }

}