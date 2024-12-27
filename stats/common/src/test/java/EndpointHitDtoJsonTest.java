import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.dto.EndpointHitDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@JsonTest
@ContextConfiguration(classes = DtoTestConfig.class)
public class EndpointHitDtoJsonTest {
    @Autowired
    private JacksonTester<EndpointHitDto> tester;

    private final Validator validator;

    public EndpointHitDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Serialize of EndpointHitDto: correct IP")
    public void shouldSerializeEndpointHitDtoToJson() throws Exception {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .id(1L)
                .app("SomeApp")
                .uri("/test")
                .ip("192.168.01.01")
                .timestamp(LocalDateTime.of(2000, 1, 1, 00, 00, 00))
                .build();

        var result = tester.write(endpointHitDto);
        var violations = validator.validate(endpointHitDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("SomeApp");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("/test");
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo("192.168.01.01");
        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo("2000-01-01 00:00:00");
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Serialize of EndpointHitDto: incorrect IP")
    public void shouldFailSerializationEndpointHitDtoToJson() throws Exception {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .id(1L)
                .app("SomeApp")
                .uri("/test")
                .ip("9999.9999.9999.9999")
                .timestamp(LocalDateTime.of(2000, 1, 1, 00, 00, 00))
                .build();

        var violations = validator.validate(endpointHitDto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deserialize of EndpointHitDto: correct IP")
    public void shouldDeserializeEndpointHitDtoFromJson() throws Exception {
        String json = "{\n" +
                "    \"id\": \"1\",\n" +
                "    \"app\": \"SomeApp\",\n" +
                "    \"uri\": \"/test\", \n" +
                "    \"ip\": \"192.168.01.01\", \n" +
                "    \"timestamp\": \"2000-01-01 00:00:00\" \n" +
                "}";

        var result = tester.parseObject(json);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getApp()).isEqualTo("SomeApp");
        assertThat(result.getUri()).isEqualTo("/test");
        assertThat(result.getIp()).isEqualTo("192.168.01.01");
        assertThat(result.getTimestamp()).isEqualTo(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
    }

    @Test
    @DisplayName("Deserialize of EndpointHitDto: incorrect IP")
    public void shouldFailDeserializationEndpointHitDtoFromJson() throws Exception {
        String json = "{\n" +
                "    \"id\": \"1\",\n" +
                "    \"app\": \"SomeApp\",\n" +
                "    \"uri\": \"/test\", \n" +
                "    \"ip\": \"9999.9999.9999.9999\", \n" +
                "    \"timestamp\": \"2000-01-01 00:00:00\" \n" +
                "}";

        EndpointHitDto result = tester.parseObject(json);
        var violations = validator.validate(result);

        assertThat(violations).isNotEmpty();
    }
}