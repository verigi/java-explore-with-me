package ru.practicum.general.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.general.enums.StateComment;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column(name = "created", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated", nullable = false)
    private LocalDateTime updatedOn;
    @Column(name = "text", nullable = false)
    @Length(min = 2, max = 500)
    private String text;

    @Column(name = "is_positive", nullable = false)
    private Boolean isPositive;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private StateComment state;

    @PrePersist
    public void onCreate() {
        state = StateComment.PENDING;
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}