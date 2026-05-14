package senac.dws.veiculos.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que o filtro devolve 429 e cabeçalhos quando o balde esgota. Usa limites baixos só nesta classe.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "app.rate-limit.enabled=true",
                "app.rate-limit.capacity=4",
                "app.rate-limit.refill-period=PT60S"
        })
class RateLimitingFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void afterCapacityExhausted_returns429WithStandardHeaders() throws Exception {
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(get("/api/greetings").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().string(RateLimitingFilter.HEADER_LIMIT, "4"))
                    .andExpect(header().exists(RateLimitingFilter.HEADER_REMAINING))
                    .andExpect(header().exists(RateLimitingFilter.HEADER_RESET));
        }

        String retryAfter = mockMvc.perform(get("/api/greetings").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(RateLimitingFilter.HEADER_REMAINING, "0"))
                .andExpect(header().exists(RateLimitingFilter.HEADER_RETRY_AFTER))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andReturn()
                .getResponse()
                .getHeader(RateLimitingFilter.HEADER_RETRY_AFTER);

        assertThat(retryAfter).isNotBlank();
        assertThat(Long.parseLong(retryAfter)).isPositive();
    }
}
