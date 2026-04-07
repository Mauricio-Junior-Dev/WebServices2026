package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Tipo de combustível (referência para motores).")
@Entity
public class FuelType {

    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do tipo de combustível (obrigatório e único)", example = "Flex (etanol/gasolina)")
    @NotBlank
    @Size(min = 1, max = 60)
    @Column(nullable = false, unique = true, length = 60)
    private String name;

    public FuelType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuelType fuelType)) return false;
        return id != null && id.equals(fuelType.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
