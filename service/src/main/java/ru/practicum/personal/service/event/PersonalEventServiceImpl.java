package ru.practicum.personal.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common.dto.event.CreateEventDto;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.update.UpdateEventUserRequestDto;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.mapper.UserMapper;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.User;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.EventRepository;
import ru.practicum.common.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class PersonalEventServiceImpl implements PersonalEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Autowired
    public PersonalEventServiceImpl(EventRepository eventRepository,
                                    CategoryRepository categoryRepository,
                                    UserRepository userRepository,
                                    EventMapper eventMapper,
                                    CategoryMapper categoryMapper,
                                    UserMapper userMapper) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    @Override
    public EventDto createEvent(Long userId, CreateEventDto createEventDto) {
        log.debug("Create event from user={}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} does not exist", userId);
            return new EntityNotFoundException("User with id " + userId + " does not exist");
        });
        Category category = categoryRepository.findById(createEventDto.getCategoryId()).orElseThrow(() -> {
            log.warn("Category with id {} does not exist", createEventDto.getCategoryId());
            return new EntityNotFoundException("Category with id " + createEventDto.getCategoryId() + " does not exist");
        });

        Event event = eventMapper.toEntity(createEventDto, category);
        event.setInitiator(user);
        eventRepository.save(event);

        return eventMapper.toDto(event, 0);
    }

    @Override
    public List<EventDto> getUserEvents(Long userId, int from, int size) {
        return null;
    }

    @Override
    public EventDto getUserEvent(Long userId, Long eventId) {
        return null;
    }


    @Override
    public EventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventAdminRequestDto) {
        return null;
    }

    @Override
    public EventDto cancelEvent(Long userId, Long eventId) {
        return null;
    }
}