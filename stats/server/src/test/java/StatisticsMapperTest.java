import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

public class StatisticsMapperTest {
    @Test
    @DisplayName("Successfully map EndpointHitDto to EndpointHit entity")
    void shouldMapDtoToEntity() {
        EndpointHitDto dto = EndpointHitDto.builder()
                .app("TestApp")
                .uri("/test")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.of(2000, 01, 01, 00, 00, 00))
                .build();

        EndpointHit entity = EndpointHitMapper.endpointHitFromDto(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getApp()).isEqualTo(dto.getApp());
        assertThat(entity.getUri()).isEqualTo(dto.getUri());
        assertThat(entity.getIp()).isEqualTo(dto.getIp());
        assertThat(entity.getTimestamp()).isEqualTo(dto.getTimestamp());
    }

    @Test
    @DisplayName("Successfully map EndpointHit entity to EndpointHitDto")
    void shouldMapEntityToDto() {
        EndpointHit entity = EndpointHit.builder()
                .id(1L)
                .app("TestApp")
                .uri("/test")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.of(2000, 01, 01, 00, 00, 00))
                .build();

        EndpointHitDto dto = EndpointHitMapper.endpointHitToDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getApp()).isEqualTo(entity.getApp());
        assertThat(dto.getUri()).isEqualTo(entity.getUri());
        assertThat(dto.getIp()).isEqualTo(entity.getIp());
        assertThat(dto.getTimestamp()).isEqualTo(entity.getTimestamp());
    }

    @Test
    @DisplayName("Null DTO when mapping to entity")
    void shouldHandleNullDtoToEntity() {
        EndpointHit entity = EndpointHitMapper.endpointHitFromDto(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Null entity when mapping to DTO")
    void shouldHandleNullEntityToDto() {
        EndpointHitDto dto = EndpointHitMapper.endpointHitToDto(null);

        assertThat(dto).isNull();
    }
}