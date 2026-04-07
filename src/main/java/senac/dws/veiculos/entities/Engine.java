package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Motor: tipo, cilindrada, potência e tipo de combustível.")
@Entity
public class Engine {
    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Denominação ou família do motor", example = "1.0 TURBO 200 Flex")
    @NotBlank
    @Size(min = 1, max = 120)
    @Column(nullable = false, length = 120)
    private String type;

    @Schema(description = "Cilindrada em litros", example = "1.0", minimum = "0")
    @NotNull
    @Min(0)
    private Double displacement;

    @Schema(description = "Potência (cv)", example = "120", minimum = "1")
    @NotNull
    @Min(1)
    private Integer horsepower;

    @Schema(description = "Tipo de combustível (ex.: {\"id\": 1})")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    public Engine() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Double displacement) {
        this.displacement = displacement;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Engine engine)) return false;
        return id != null && id.equals(engine.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
