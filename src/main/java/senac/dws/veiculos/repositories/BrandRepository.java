package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    //Busca por nome
    List<Brand> findByNameContainingIgnoreCase(String name);

}
