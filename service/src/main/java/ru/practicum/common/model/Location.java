package ru.practicum.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    @Column(name = "location_lat")
    private Double lat;
    @Column(name = "location_lon")
    private Double lon;
}