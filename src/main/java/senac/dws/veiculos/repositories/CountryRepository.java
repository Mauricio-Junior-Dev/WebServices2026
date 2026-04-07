package senac.dws.veiculos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import senac.dws.veiculos.entities.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long>, PagingAndSortingRepository<Country, Long> {

    List<Country> findByNameContainingIgnoreCase(String name);

    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
