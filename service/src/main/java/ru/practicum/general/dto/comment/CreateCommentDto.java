package ru.practicum.general.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    @NotBlank(message = "Comment text must not be null or empty")
    @Length(min = 2, max = 500)
    private String text;
    @NotNull(message = "Comment estimation must not be null")
    private Boolean isPositive;
}