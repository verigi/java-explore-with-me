package ru.practicum.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.enums.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false, length = 250)
    @Size(min = 2, max = 250)
    private String title;
    @Column(name = "annotation", nullable = false, length = 300)
    @Size(min = 10, max = 300)
    private String annotation;
    @Column(name = "description", nullable = false, length = 500)
    @Size(min = 10, max = 500)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "published")
    private LocalDateTime published;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;
    @Column(name = "paid", nullable = false)
    private boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private int participantLimit;
    @Column(name = "moderation_request", nullable = false)
    private boolean moderationRequest;
    @Column(name = "views", nullable = false)
    private int views;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Embedded
    private Location location;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ParticipationRequest> requests = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        created = LocalDateTime.now();
        state = EventState.PENDING;
    }
}