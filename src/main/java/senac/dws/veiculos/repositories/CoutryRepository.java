package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Country;

public interface CoutryRepository extends JpaRepository<Country, Long> {
}
