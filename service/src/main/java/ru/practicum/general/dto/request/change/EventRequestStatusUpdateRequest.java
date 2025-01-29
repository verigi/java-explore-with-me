package ru.practicum.general.dto.request.change;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.general.enums.StateRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Request ids must not be null")
    private List<Long> requestIds;
    @NotNull(message = "Request status must not be null")
    private StateRequest status;
}