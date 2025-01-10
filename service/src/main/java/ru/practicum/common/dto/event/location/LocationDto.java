package ru.practicum.common.dto.event.location;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Double lat;
    private Double lon;
}