package ru.practicum.general.dto.comment.update;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.general.enums.StateAction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentAdminRequest implements UpdateComment {
    @NotNull(message = "State action must not be null")
    private StateAction stateAction;
}