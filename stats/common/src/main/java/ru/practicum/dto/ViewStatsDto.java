package ru.practicum.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"app", "uri", "hits"})
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}