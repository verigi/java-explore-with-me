package ru.practicum.general.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.general.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.general.enums.StateAction;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.exceptions.CustomConflictException;
import ru.practicum.general.exceptions.DuplicationException;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ValidationHandler {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CompilationRepository compilationRepository;

    @Autowired
    public ValidationHandler(UserRepository userRepository,
                             EventRepository eventRepository,
                             CategoryRepository categoryRepository,
                             ParticipationRequestRepository participationRequestRepository,
                             CompilationRepository compilationRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.participationRequestRepository = participationRequestRepository;
        this.compilationRepository = compilationRepository;
    }

    // common
    public <T> T findEntityById(JpaRepository<T, Long> repository, Long id, String entityName) {
        log.debug("Fetching {} with id {}", entityName, id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("{} with id {} does not exist", entityName, id);
                    return new EntityNotFoundException(entityName + " with id " + id + " does not exist");
                });
    }

    // user
    public void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Duplication of user found: email={}", email);
            throw new DuplicationException("User with email " + email + " already exists");
        }
    }

    // category
    public void validateCategoryName(String catName) {
        if (categoryRepository.findByName(catName).isPresent()) {
            log.warn("Duplication of category found: name={}", catName);
            throw new DuplicationException("Category with name " + catName + " already exists");
        }
    }

    public void validateRelatedEvents(Long catId) {
        List<Event> relatedEvents = eventRepository.findByCategory_Id(catId);
        if (!relatedEvents.isEmpty()) {
            log.warn("Related event(-s) found for category id={}", catId);
            throw new CustomConflictException("Category with id " + catId + " related to event(-s). Denied to delete category");
        }
    }

    // participation request
    public void validateParticipationRequest(Event event, User user) {
        long confirmedRequests = participationRequestRepository.countConfirmedRequests(event.getId());
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() == confirmedRequests) {
            log.warn("The limit of participant has been reached: limit={}, confirmed requests={}",
                    event.getParticipantLimit(), confirmedRequests);
            throw new CustomConflictException("Participant limit has been exceeded: " + event.getParticipantLimit());
        }

        if (user.getId().equals(event.getInitiator().getId())) {
            log.warn("Attempt to send participation request to own event: event initiator id={}, requester id={}",
                    event.getInitiator().getId(), user.getId());
            throw new CustomConflictException("Participation request must not be send to your own event");
        }

        if (participationRequestRepository.existsByEventIdAndRequesterId(event.getId(), user.getId())) {
            log.warn("Duplication of participation request: event id={}, user id={}", event.getId(), user.getId());
            throw new CustomConflictException("There can be only one participation request from one user");
        }
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            log.warn("Attempt to send participation request to non-published event: event state={}", event.getState());
            throw new CustomConflictException("Participation request can only be submitted for published events");
        }
    }

    // event
    public void validateAdminEventDate(Event event, LocalDateTime newEventDate) {
        if (newEventDate != null &&
                newEventDate.isBefore(event.getCreatedOn())) {
            throw new IllegalArgumentException("");
        }
        if (event.getPublishedOn() != null &&
                newEventDate != null &&
                event.getPublishedOn().plusHours(1).isAfter(newEventDate)) {
            throw new CustomConflictException("Update event date should be later than 1 hour after publishing");
        }
    }

    public void validateAdminEventState(Event event, UpdateEventAdminRequestDto dto) {
        if (!StateEvent.PENDING.equals(event.getState()) && StateAction.PUBLISH_EVENT.equals(dto.getStateAction())) {
            throw new CustomConflictException("Event must be in 'PENDING' state to be published");
        }
        if (StateEvent.PUBLISHED.equals(event.getState()) && StateAction.REJECT_EVENT.equals(dto.getStateAction())) {
            throw new CustomConflictException("Event must not be 'PUBLISHED' to be rejected");
        }
    }

    public void validateUserEventState(Event event) {
        if (!StateEvent.CANCELED.equals(event.getState()) && !StateEvent.PENDING.equals(event.getState())) {
            throw new CustomConflictException("Event must be in 'CANCELED' or 'PENDING' state to be updated");
        }
    }

    public void validateUserEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new IllegalArgumentException("Update event date should be later than 2 hours after current time");
        }
    }
}