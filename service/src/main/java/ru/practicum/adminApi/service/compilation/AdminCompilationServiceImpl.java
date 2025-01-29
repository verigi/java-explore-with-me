package ru.practicum.adminApi.service.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.general.dto.compilation.CreateCompilationDto;
import ru.practicum.general.dto.compilation.UpdateCompilationDto;
import ru.practicum.general.mapper.CompilationMapper;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.model.Compilation;
import ru.practicum.general.model.Event;
import ru.practicum.general.repository.CompilationRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.util.ValidationHandler;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ValidationHandler validationHandler;

    @Autowired
    public AdminCompilationServiceImpl(CompilationRepository compilationRepository,
                                       CompilationMapper compilationMapper,
                                       EventRepository eventRepository,
                                       EventMapper eventMapper,
                                       ValidationHandler validationHandler) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.validationHandler = validationHandler;
    }


    @Override
    public CompilationDto createCompilation(CreateCompilationDto createCompilationDto) {
        log.debug("Attempting to create compilation: title={}, pinned={}, events={}",
                createCompilationDto.getTitle(),
                createCompilationDto.getPinned(),
                createCompilationDto.getEvents());
        List<Event> events = createCompilationDto.getEvents().stream()
                .map(eventId -> validationHandler.findEntityById(eventRepository, eventId, "Event"))
                .collect(Collectors.toList());

        Compilation compilation = compilationMapper.toEntity(createCompilationDto, events);
        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.debug("Attempting to delete compilation: id={}", compId);
        Compilation compilation = validationHandler.findEntityById(compilationRepository, compId, "Compilation");

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        log.debug("Attempting to update compilation: id={}", compId);
        Compilation compilation = validationHandler.findEntityById(compilationRepository, compId, "Compilation");
        Compilation updCompilation = compilationMapper.updateEntity(compilation, updateCompilationDto);
        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = updateCompilationDto.getEvents().stream()
                    .map(eventId -> validationHandler.findEntityById(eventRepository, eventId, "Event"))
                    .collect(Collectors.toList());
            updCompilation.setEvents(events);
        }

        compilationRepository.save(updCompilation);
        return compilationMapper.toDto(updCompilation);
    }
}