package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "País utilizado como referência geográfica para marcas.")
@Entity
public class Country {

    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do país (obrigatório e único no sistema)", example = "Brasil", minLength = 2, maxLength = 80)
    @NotBlank
    @Size(min = 2, max = 80)
    @Column(unique = true, nullable = false, length = 80)
    private String name;

    public Country() {
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
        if (!(o instanceof Country country)) return false;
        return id != null && id.equals(country.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
