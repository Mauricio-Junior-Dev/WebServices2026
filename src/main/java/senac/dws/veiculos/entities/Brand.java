package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Marca automotiva vinculada a um país de origem.")
@Entity
public class Brand {
    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome da marca", example = "Fiat")
    @NotBlank
    @Size(min = 1, max = 80)
    @Column(nullable = false, length = 80)
    private String name;

    @Schema(description = "País de origem (ex.: {\"id\": 1})")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Brand() {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand brand)) return false;
        return id != null && id.equals(brand.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
