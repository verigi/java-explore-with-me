package ru.practicum.common.dto.error;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDto {
    private String message;
    private String reason;
    private String status;
    private LocalDateTime timestamp;
    private List<String> errors;
}