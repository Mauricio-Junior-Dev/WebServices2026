package senac.dws.veiculos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Veículo da frota: dados comerciais, status de estoque e associações com marca, categoria, motor, documentação e acessórios.")
@Entity
public class Vehicle {
    @Schema(description = "Chave primária", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome ou modelo comercial", example = "Pulse Impetus 1.3")
    @NotBlank
    @Size(min = 1, max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @Schema(description = "Ano do modelo", example = "2024", minimum = "1900", maximum = "2100")
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(name = "\"year\"")
    private Integer year;

    @Schema(description = "Preço de referência", example = "94990.00", minimum = "0")
    @NotNull
    @Min(0)
    private Double price;

    @Schema(description = "Situação comercial do veículo na frota", example = "DISPONIVEL")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Schema(description = "Marca (na API REST normalmente basta informar o id, ex.: {\"id\": 1})")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY: carrega o objeto Brand somente quando necessário
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Schema(description = "Categoria do veículo (ex.: {\"id\": 2})")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Schema(description = "Motor associado (ex.: {\"id\": 1})")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;

    @Schema(description = "Documentação 1:1; opcional na criação se cadastrada depois em /documentacoes")
    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Documentacao documentacao;

    @Schema(description = "Conjunto de acessórios (cada item pode ser apenas {\"id\": 1})")
    @ManyToMany
    @JoinTable(
            name = "vehicle_acessorio",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "acessorio_id")
    )
    private Set<Acessorio> acessorios = new HashSet<>();

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Documentacao getDocumentacao() {
        return documentacao;
    }

    public void setDocumentacao(Documentacao documentacao) {
        this.documentacao = documentacao;
        if (documentacao != null) {
            documentacao.setVehicle(this);
        }
    }

    public Set<Acessorio> getAcessorios() {
        return acessorios;
    }

    public void setAcessorios(Set<Acessorio> acessorios) {
        this.acessorios = acessorios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return id != null && id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
