package ru.practicum.general.dto.request.participation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateParticipationRequestDto {
    @NotNull(message = "Event ID must not be null")
    private Long eventId;
}