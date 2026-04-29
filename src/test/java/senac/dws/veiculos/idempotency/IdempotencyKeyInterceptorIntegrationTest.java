package senac.dws.veiculos.idempotency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IdempotencyKeyInterceptorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postWithSameIdempotencyKeyTwice_secondReturns409() throws Exception {
        String key = UUID.randomUUID().toString();
        String name = "Pais-Idem-" + UUID.randomUUID();
        String json = "{\"name\":\"" + name + "\"}";

        mockMvc.perform(post("/api/v1/countries")
                        .header(IdempotencyKeyInterceptor.HEADER_NAME, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/countries")
                        .header(IdempotencyKeyInterceptor.HEADER_NAME, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Esta operação já foi processada ou está em andamento"));
    }

    @Test
    void postWithoutIdempotencyKey_stillWorks() throws Exception {
        String name = "Pais-SemHeader-" + UUID.randomUUID();
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name));
    }
}
