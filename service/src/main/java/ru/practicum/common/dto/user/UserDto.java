package ru.practicum.common.dto.user;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
}