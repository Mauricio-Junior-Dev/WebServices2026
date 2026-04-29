package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.IdempotencyKey;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
}
