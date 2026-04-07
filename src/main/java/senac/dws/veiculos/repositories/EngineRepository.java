package senac.dws.veiculos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import senac.dws.veiculos.entities.Engine;

public interface EngineRepository extends JpaRepository<Engine, Long>, PagingAndSortingRepository<Engine, Long> {

    Page<Engine> findByTypeContainingIgnoreCase(String type, Pageable pageable);
}
