package ru.practicum.adminApi.service.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.compilation.CompilationDto;
import ru.practicum.general.dto.compilation.CreateCompilationDto;
import ru.practicum.general.dto.compilation.UpdateCompilationDto;
import ru.practicum.general.mapper.CompilationMapper;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.model.Compilation;
import ru.practicum.general.model.Event;
import ru.practicum.general.repository.CompilationRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EntityHandler entityHandler;

    @Autowired
    public AdminCompilationServiceImpl(CompilationRepository compilationRepository,
                                       CompilationMapper compilationMapper,
                                       EventRepository eventRepository,
                                       EventMapper eventMapper,
                                       EntityHandler entityHandler) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.entityHandler = entityHandler;
    }


    @Override
    @Transactional
    public CompilationDto createCompilation(CreateCompilationDto createCompilationDto) {
        log.debug("Attempting to create compilation: title={}, pinned={}, events={}",
                createCompilationDto.getTitle(),
                createCompilationDto.getPinned(),
                createCompilationDto.getEvents());
        List<Event> events = createCompilationDto.getEvents().stream()
                .map(eventId -> entityHandler.findEntityById(eventRepository, eventId, "Event"))
                .collect(Collectors.toList());

        Compilation compilation = compilationMapper.toEntity(createCompilationDto, events);
        compilationRepository.save(compilation);

        log.debug("Compilation created. Id={}", compilation.getId());
        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("Attempting to delete compilation. Id={}", compId);

        Compilation compilation = entityHandler.findEntityById(compilationRepository, compId, "Compilation");
        compilationRepository.delete(compilation);

        log.debug("Compilation deleted. Id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        log.debug("Attempting to update compilation. Id={}", compId);

        Compilation compilation = entityHandler.findEntityById(compilationRepository, compId, "Compilation");
        compilation = compilationMapper.updateEntity(compilation, updateCompilationDto);
        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = updateCompilationDto.getEvents().stream()
                    .map(eventId -> entityHandler.findEntityById(eventRepository, eventId, "Event"))
                    .collect(Collectors.toList());
            compilation.setEvents(events);
        }
        compilationRepository.flush();

        log.debug("Compilation updated. Id={}", compId);
        return compilationMapper.toDto(compilation);
    }
}