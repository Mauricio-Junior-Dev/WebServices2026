package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
