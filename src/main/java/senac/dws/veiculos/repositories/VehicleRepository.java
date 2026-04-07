package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import senac.dws.veiculos.entities.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, PagingAndSortingRepository<Vehicle, Long> {

    List<Vehicle> findByNameContainingIgnoreCase(String name);

    List<Vehicle> findByYear(Integer year);

    List<Vehicle> findByBrandId(Long brandId);

    List<Vehicle> findByPriceLessThan(Double price);

    @Query("""
            select distinct v from Vehicle v
            left join fetch v.acessorios
            left join fetch v.documentacao
            left join fetch v.brand
            left join fetch v.category
            left join fetch v.engine
            where v.id = :id
            """)
    Optional<Vehicle> findDetailById(@Param("id") Long id);
}
