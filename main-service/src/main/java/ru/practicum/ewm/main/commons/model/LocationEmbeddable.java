package ru.practicum.ewm.main.commons.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class LocationEmbeddable {

    private Double lat;

    private Double lon;
}
