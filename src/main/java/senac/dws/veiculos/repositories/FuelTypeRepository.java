package senac.dws.veiculos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import senac.dws.veiculos.entities.FuelType;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long>, PagingAndSortingRepository<FuelType, Long> {

    Page<FuelType> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
