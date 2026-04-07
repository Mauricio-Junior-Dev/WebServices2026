package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status comercial do veículo", allowableValues = {"DISPONIVEL", "RESERVADO", "VENDIDO"})
public enum Status {
    DISPONIVEL,
    RESERVADO,
    VENDIDO
}
