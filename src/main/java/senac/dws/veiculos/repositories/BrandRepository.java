package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Brand;

public interface BrandRepository extends JpaRepository<Brand, String> {
}
