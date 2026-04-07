package senac.dws.veiculos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import senac.dws.veiculos.entities.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long>, PagingAndSortingRepository<Brand, Long> {

    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Brand> findByNameContainingIgnoreCase(String name);
}
