package ru.practicum.general.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    @Column(name = "location_lat")
    private Double lat;
    @Column(name = "location_lon")
    private Double lon;
}