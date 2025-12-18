package ru.practicum.ewm.main.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.commons.enums.RequestStatus;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RequestStatus status;
}
