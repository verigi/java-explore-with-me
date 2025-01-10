package ru.practicum.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.enums.RequestState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateParticipationRequestDto {
    @NotNull(message = "Event ID must not be null")
    private Long eventId;
}