package senac.dws.veiculos.api.v2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import senac.dws.veiculos.entities.Vehicle;

@Schema(description = "Resumo de veículo na API v2 (somente listagem).")
public record VehicleListItemV2Dto(
        @Schema(description = "Identificador", example = "1") Long id,
        @Schema(description = "Nome ou modelo comercial", example = "Corolla XEi 2.0") String name,
        @Schema(description = "Preço de referência", example = "164990.00") Double price
) {
    public static VehicleListItemV2Dto from(Vehicle v) {
        return new VehicleListItemV2Dto(v.getId(), v.getName(), v.getPrice());
    }
}
