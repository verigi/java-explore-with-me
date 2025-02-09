package ru.practicum.general.dto.comment.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentUserRequest implements UpdateComment {
    @NotBlank(message = "Comment text must not be blank")
    @Length(min = 2, max = 500)
    private String text;
}