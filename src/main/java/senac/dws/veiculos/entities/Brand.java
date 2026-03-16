package senac.dws.veiculos.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "country_id")
    private Country country;
}
