package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import senac.dws.veiculos.entities.Documentacao;

import java.util.Optional;

public interface DocumentacaoRepository extends JpaRepository<Documentacao, Long>, PagingAndSortingRepository<Documentacao, Long> {

    @Query("select d from Documentacao d join fetch d.vehicle where d.id = :id")
    Optional<Documentacao> findDetailById(@Param("id") Long id);

    boolean existsByNumeroRegistro(String numeroRegistro);

    boolean existsByNumeroRegistroAndIdNot(String numeroRegistro, Long id);
}
