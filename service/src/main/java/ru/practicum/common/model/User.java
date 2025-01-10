package ru.practicum.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "name", nullable = false, length = 250)
    @Size(min = 2, max = 250)
    private String name;
    @Column(name = "email", nullable = false, length = 250, unique = true)
    @Size(min = 2, max = 250)
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