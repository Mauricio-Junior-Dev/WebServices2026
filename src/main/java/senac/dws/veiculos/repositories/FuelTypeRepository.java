package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.FuelType;

public interface FuelTypeRepository  extends JpaRepository<FuelType, Long> {
}
