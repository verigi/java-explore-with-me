package ru.practicum.common.dto.category;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class CategoryDto {
    private Long id;
    private String name;
}