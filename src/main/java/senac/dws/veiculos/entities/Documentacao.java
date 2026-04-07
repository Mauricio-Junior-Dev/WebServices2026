package senac.dws.veiculos.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Documentação administrativa vinculada a um veículo (relação 1:1).")
@Entity
@Table(name = "documentacao")
public class Documentacao {

    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Número de registro da documentação (obrigatório e único em todo o sistema)", example = "BR-REG-2024-8899")
    @NotBlank
    @Size(min = 3, max = 40)
    @Column(nullable = false, unique = true, length = 40)
    private String numeroRegistro;

    @Schema(description = "Observações gerais", example = "Primeiro emplacamento em SP")
    @Size(max = 2000)
    @Column(length = 2000)
    private String observacao;

    @Schema(description = "Veículo associado — na criação via POST /documentacoes informe apenas {\"id\": <id do veículo>}. Não aparece na resposta JSON.")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", unique = true, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Vehicle vehicle;

    public Documentacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Documentacao that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
