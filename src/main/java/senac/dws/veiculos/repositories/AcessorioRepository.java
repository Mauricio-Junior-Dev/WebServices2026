package senac.dws.veiculos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import senac.dws.veiculos.entities.Acessorio;

import java.util.List;

public interface AcessorioRepository extends JpaRepository<Acessorio, Long>, PagingAndSortingRepository<Acessorio, Long> {

    Page<Acessorio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    List<Acessorio> findByNomeContainingIgnoreCase(String nome);
}
