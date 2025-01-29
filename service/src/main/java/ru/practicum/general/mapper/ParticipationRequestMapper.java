package ru.practicum.general.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.ParticipationRequest;
import ru.practicum.general.model.User;

import java.time.LocalDateTime;

@Component
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest participationRequest) {
        return participationRequest == null ? null : ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .status(participationRequest.getStatus())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .build();
    }

    public ParticipationRequest toEntity(User user, Event event) {
        return event == null ? null : ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .build();
    }




}