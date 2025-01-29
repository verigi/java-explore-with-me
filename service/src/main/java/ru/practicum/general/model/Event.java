package ru.practicum.general.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.general.enums.StateEvent;

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
    @Column(name = "title", nullable = false)
    @Length(min = 3, max = 120)
    private String title;
    @Column(name = "annotation", nullable = false)
    @Length(min = 20, max = 2000)
    private String annotation;
    @Column(name = "description", nullable = false, length = 500)
    @Length(min = 20, max = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "created", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private StateEvent state;
    @Column(name = "paid", nullable = false)
    private boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private int participantLimit;
    @Column(name = "moderation_request", nullable = false)
    private boolean requestModeration;
    @Column(name = "views", nullable = false)
    @Min(0)
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
        createdOn = LocalDateTime.now();
        state = StateEvent.PENDING;
    }
}