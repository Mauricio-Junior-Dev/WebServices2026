package senac.dws.veiculos.versioning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import senac.dws.veiculos.api.ApiVersionHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante o comportamento do versionamento por cabeçalho na listagem de veículos.
 * <p>Leia também {@link senac.dws.veiculos.api.ApiVersionHeaders} e os dois métodos {@code listV*}
 * em {@link senac.dws.veiculos.controllers.VehicleController}.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class VehicleApiVersionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listWithoutVersion_returnsHateoasPagedModel() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listWithVersion1_returnsHateoasPagedModel() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("size", "1")
                        .header(ApiVersionHeaders.NAME, ApiVersionHeaders.V1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listWithVersion2_returnsSpringDataPageWithFlatItems() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("size", "1")
                        .header(ApiVersionHeaders.NAME, ApiVersionHeaders.V2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].price").exists())
                .andExpect(jsonPath("$.content[0]._links").doesNotExist());
    }

    @Test
    void listWithUnknownVersion_returns400() throws Exception {
        mockMvc.perform(get("/api/vehicles").header(ApiVersionHeaders.NAME, "99"))
                .andExpect(status().isBadRequest());
    }
}
