package ru.practicum.general.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.general.dto.event.CreateEventDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.EventShortDto;
import ru.practicum.general.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.general.dto.event.update.UpdateEventDto;
import ru.practicum.general.dto.event.update.UpdateEventUserRequestDto;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.enums.StateRequest;
import ru.practicum.general.model.Category;
import ru.practicum.general.model.Event;
import ru.practicum.general.repository.CategoryRepository;

import java.time.LocalDateTime;

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
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .initiator(userMapper.toShortDto(event.getInitiator()))
                .category(categoryMapper.toDto(event.getCategory()))
                .location(event.getLocation())
                .confirmedRequests((int) event.getRequests().stream()
                        .filter(request -> request.getStatus() == StateRequest.CONFIRMED)
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
                .initiator(userMapper.toShortDto(event.getInitiator()))
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(Math.toIntExact(event.getRequests().stream()
                        .filter(request -> request.getStatus().equals(StateRequest.CONFIRMED))
                        .count()))
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
                .requestModeration(createEventDto.getRequestModeration())
                .category(category)
                .location(createEventDto.getLocation())
                .build();
    }

    public Event updateEntity(Event event, UpdateEventDto dto) {
        if (dto instanceof UpdateEventAdminRequestDto adminRequestDto) {
            applyAdminChanges(event, adminRequestDto);
        } else if (dto instanceof UpdateEventUserRequestDto userRequestDto) {
            applyUserChanges(event, userRequestDto);
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + dto.getClass().getSimpleName());
        }
        return event;
    }

    private void applyAdminChanges(Event event, UpdateEventAdminRequestDto dto) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getModerationRequest() != null) event.setRequestModeration(dto.getModerationRequest());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + dto.getCategoryId() + " does not exists"));
            event.setCategory(category);
        }
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(StateEvent.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    event.setState(StateEvent.CANCELED);
                }
                default ->
                        throw new IllegalArgumentException("Incorrect action state for admin: " + dto.getStateAction());
            }
        }
    }

    private void applyUserChanges(Event event, UpdateEventUserRequestDto dto) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getModerationRequest() != null) event.setRequestModeration(dto.getModerationRequest());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + dto.getCategoryId() + " does not exists"));
            event.setCategory(category);
        }
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> {
                    event.setState(StateEvent.PENDING);
                }
                case CANCEL_REVIEW -> {
                    event.setState(StateEvent.CANCELED);
                }
                default ->
                        throw new IllegalArgumentException("Incorrect action state for user: " + dto.getStateAction());
            }
        }
    }
}