import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.dto.ViewStatsDto;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@ContextConfiguration(classes = DtoTestConfig.class)
public class ViewStatsDtoTest {

    @Autowired
    private JacksonTester<ViewStatsDto> tester;

    @Test
    @DisplayName("Serialize of ViewStatsDto")
    public void shouldSerializeViewStatsDtoToJson() throws Exception {
        ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app("SomeApp")
                .uri("/test")
                .hits(1L)
                .build();

        var result = tester.write(viewStatsDto);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("SomeApp");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("/test");
        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(1);
    }

    @Test
    @DisplayName("Deserialize of ViewStatsDto")
    public void shouldDeserializeViewStatsDtoFromJson() throws Exception {
        String json = "{\n" +
                "    \"app\": \"SomeApp\",\n" +
                "    \"uri\": \"/test\", \n" +
                "    \"hits\": \"1\" \n" +
                "}";

        var result = tester.parseObject(json);

        assertThat(result.getApp()).isEqualTo("SomeApp");
        assertThat(result.getUri()).isEqualTo("/test");
        assertThat(result.getHits()).isEqualTo(1);
    }
}