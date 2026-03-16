package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Engine;

public interface EngineRepository extends JpaRepository<Engine, Long> {
}
