package ru.practicum.common.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.common.dto.event.CreateEventDto;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.EventShortDto;
import ru.practicum.common.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.common.enums.EventState;
import ru.practicum.common.enums.RequestState;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Event;
import ru.practicum.common.repository.CategoryRepository;

import java.util.NoSuchElementException;

@Component
public class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public EventMapper(UserMapper userMapper, CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    public EventDto toDto(Event event, int views) {
        return event == null ? null : EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .created(event.getCreated())
                .published(event.getPublished())
                .state(event.getState())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .moderationRequest(event.isModerationRequest())
                .initiatorDto(userMapper.toShortDto(event.getInitiator()))
                .categoryDto(categoryMapper.toDto(event.getCategory()))
                .location(event.getLocation())
                .confirmedRequests((int) event.getRequests().stream()
                        .filter(request -> request.getState() == RequestState.CONFIRMED)
                        .count())
                .views(views)
                .build();
    }

    public EventShortDto toShortDto(Event event, int views) {
        return event == null ? null : EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .initiatorDto(userMapper.toShortDto(event.getInitiator()))
                .categoryDto(categoryMapper.toDto(event.getCategory()))
                .views(views)
                .build();
    }

    public Event toEntity(CreateEventDto createEventDto, Category category) {
        return createEventDto == null ? null : Event.builder()
                .title(createEventDto.getTitle())
                .annotation(createEventDto.getAnnotation())
                .description(createEventDto.getDescription())
                .eventDate(createEventDto.getEventDate())
                .paid(createEventDto.isPaid())
                .participantLimit(createEventDto.getParticipantLimit())
                .moderationRequest(createEventDto.isModerationRequest())
                .category(category)
                .location(createEventDto.getLocation())
                .build();
    }

    public Event updateEntity(Event event, UpdateEventAdminRequestDto dto) {
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getModerationRequest() != null) {
            event.setModerationRequest(dto.getModerationRequest());
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + dto.getCategoryId() + " does not exists"));
            event.setCategory(category);
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getActionState() != null) {
            switch (dto.getActionState()) {
                case PUBLISH_EVENT -> event.setState(EventState.PUBLISHED);
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
                default -> throw new IllegalArgumentException("Incorrect action state: " + dto.getActionState());
            }
        }
        return event;
    }
}