package senac.dws.veiculos.controllers.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import senac.dws.veiculos.api.ApiErrorResponse;
import senac.dws.veiculos.api.v2.dto.VehicleListItemV2Dto;
import senac.dws.veiculos.services.VehicleService;

@Tag(name = "Vehicle v2", description = "Listagem simplificada de veículos (contrato enxuto: id, name, price).")
@RestController
@RequestMapping("/api/v2/vehicles")
public class VehicleV2Controller {

    private final VehicleService vehicleService;

    public VehicleV2Controller(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(
            summary = "[v2] Lista veículos (resumo)",
            description = "Paginação e ordenação: page (base 0), size e sort. Cada item contém apenas id, name e price.")
    @ApiResponse(responseCode = "200", description = "Página de resumos de veículos")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<Page<VehicleListItemV2Dto>> list(Pageable pageable) {
        Page<VehicleListItemV2Dto> page = vehicleService.findAll(pageable).map(VehicleListItemV2Dto::from);
        return ResponseEntity.ok(page);
    }
}
