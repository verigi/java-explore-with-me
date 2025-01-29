package ru.practicum.general.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @Length(min = 2, max = 250)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    @Length(min = 6, max = 254)
    @Email
    private String email;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;
    @OneToMany(mappedBy = "initiator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipationRequest> requests = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updated = LocalDateTime.now();
    }

    public void addEvent(Event event) {
        events.add(event);
        event.setInitiator(this);
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setInitiator(null);
    }

    public void addRequest(ParticipationRequest request) {
        requests.add(request);
        request.setRequester(this);
    }

    public void removeRequest(ParticipationRequest request) {
        requests.remove(request);
        request.setRequester(null);
    }
}