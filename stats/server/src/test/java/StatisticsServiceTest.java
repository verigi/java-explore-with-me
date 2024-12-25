import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.DatabaseEndpointHitException;
import ru.practicum.exception.InvalidEndpointHitException;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatisticRepository;
import ru.practicum.service.StatisticsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {
    @Mock
    private StatisticRepository repository;

    @InjectMocks
    private StatisticsServiceImpl service;

    @Test
    @DisplayName("Save hit successfully")
    void shouldSaveHitSuccessfully() {
        EndpointHitDto dto = new EndpointHitDto(1L, "app", "/test", "192.168.0.1", LocalDateTime.now());
        EndpointHit entity = new EndpointHit(1L, "app", "/test", "192.168.0.1", LocalDateTime.now());

        when(repository.save(any(EndpointHit.class))).thenReturn(entity);

        EndpointHitDto result = service.saveHit(dto);

        assertNotNull(result);
        assertEquals(dto.getApp(), result.getApp());
    }

    @Test
    @DisplayName("Exception when saving null DTO")
    void shouldThrowExceptionWhenSavingNull() {
        assertThrows(InvalidEndpointHitException.class, () -> service.saveHit(null));
    }

    @Test
    @DisplayName("Exception on DB save error")
    void shouldThrowExceptionOnDbSaveError() {
        EndpointHitDto dto = new EndpointHitDto(1L, "app", "/test", "192.168.0.1", LocalDateTime.now());

        when(repository.save(any(EndpointHit.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DatabaseEndpointHitException.class, () -> service.saveHit(dto));
    }

    @Test
    @DisplayName("Return common hits successfully")
    void shouldReturnCommonHits() {
        List<ViewStatsDto> mockStats = List.of(new ViewStatsDto("app", "/test", 10L));
        when(repository.getHits(any(), any(), any())).thenReturn(mockStats);

        List<ViewStatsDto> result = service.getHits(LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, false);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Return unique hits successfully")
    void shouldReturnUniqueHits() {
        List<ViewStatsDto> mockStats = List.of(new ViewStatsDto("app", "/test", 5L));
        when(repository.getUniqueHits(any(), any(), any())).thenReturn(mockStats);

        List<ViewStatsDto> result = service.getHits(LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, true);

        assertEquals(1, result.size());
    }
}