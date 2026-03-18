package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByNameContainingIgnoreCase(String name);

    List<Vehicle> findByYear(Integer year);

    List<Vehicle> findByBrandId(Long brandId);

    List<Vehicle> findByPriceLessThan(Double price);

}
