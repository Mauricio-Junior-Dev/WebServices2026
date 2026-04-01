package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {
    // Busca por nome
    List<Country> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
