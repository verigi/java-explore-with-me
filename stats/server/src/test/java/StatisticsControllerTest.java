import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.StatisticsServerApp;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(classes = StatisticsServerApp.class)
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Valid GET request. Start and end only")
    void shouldReturnOkResponseForGetStatsStartEnd() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", "2000-01-02 00:00:00"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Valid GET request. Start, end and uri")
    void shouldReturnOkResponseForGetStatsStartEndUri() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", "2000-01-02 00:00:00")
                        .param("uri", "/test"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Valid GET request. Start, end and unique")
    void shouldReturnOkResponseForGetStatsStartEndUnique() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", "2000-01-02 00:00:00")
                        .param("unique", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Valid GET request. Full request (start, end, uri, unique)")
    void shouldReturnOkResponseForGetStatsFullRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", "2000-01-02 00:00:00")
                        .param("uri", "/test")
                        .param("unique", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Invalid GET request. Missing start param")
    void shouldReturnBadRequestResponseForMissingStartTime() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("end", "2000-01-01 00:01:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. Missing end param")
    void shouldReturnBadRequestResponseForMissingEndTime() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:01:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. Start after end")
    void shouldReturnBadRequestResponseForStartAfterEndTime() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:01:00")
                        .param("end", "2000-01-01 00:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. Start is null")
    void shouldReturnBadRequestResponseForStartIsNull() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", (String) null)
                        .param("end", "2000-01-01 00:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. End is null")
    void shouldReturnBadRequestResponseForEndIsNull() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", (String) null))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. Invalid start format")
    void shouldReturnBadRequestResponseForStartInvalid() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "Some invalid time")
                        .param("end", "2000-01-01 00:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid GET request. Invalid end format")
    void shouldReturnBadRequestResponseForEndInvalid() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 00:00:00")
                        .param("end", "Some invalid time"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Valid POST request")
    void shouldReturnCreatedResponseForSaveHit() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"app\": \"SomeApp\", " +
                                "\"uri\": \"/test\", " +
                                "\"ip\": \"192.168.01.01\", " +
                                "\"timestamp\": \"2000-01-01 00:01:00\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Invalid POST request. Missing app field")
    void shouldReturnBadRequestForSaveHitWithNoApp() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"uri\": \"/test\", " +
                                "\"ip\": \"192.168.01.01\", " +
                                "\"timestamp\": \"2000-01-01 00:01:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid POST request. Missing ip field")
    void shouldReturnBadRequestForSaveHitWithNoUri() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"app\": \"SomeApp\", " +
                                "\"ip\": \"192.168.01.01\", " +
                                "\"timestamp\": \"2000-01-01 00:01:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid POST request. Missing ip field")
    void shouldReturnBadRequestForSaveHitWithNoIp() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"app\": \"SomeApp\", " +
                                "\"uri\": \"/test\", " +
                                "\"timestamp\": \"2000-01-01 00:01:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid POST request. Missing timestamp field")
    void shouldReturnBadRequestForSaveHitWithNoTimestamp() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"app\": \"SomeApp\", " +
                                "\"uri\": \"/test\", " +
                                "\"ip\": \"192.168.01.01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid POST request. Timestamp field invalid format")
    void shouldReturnBadRequestForSaveHitWithTimestampInvalid() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType("application/json")
                        .content("{\"app\": \"SomeApp\", " +
                                "\"uri\": \"/test\", " +
                                "\"ip\": \"192.168.01.01\", " +
                                "\"timestamp\": \"Help me, pls\"}"))
                .andExpect(status().isInternalServerError());
    }
}