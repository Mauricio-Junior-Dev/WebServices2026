package senac.dws.veiculos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Acessório opcional associável a vários veículos.")
@Entity
@Table(name = "acessorio")
public class Acessorio {

    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do acessório (obrigatório e único)", example = "Ar-condicionado automático")
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Schema(description = "Detalhes opcionais", example = "Dual zone, filtro de pólen")
    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @Schema(description = "Lista de veículos (não exposta na serialização JSON)", hidden = true)
    @ManyToMany(mappedBy = "acessorios")
    @JsonIgnore
    private Set<Vehicle> vehicles = new HashSet<>();

    public Acessorio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acessorio acessorio)) return false;
        return id != null && id.equals(acessorio.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
