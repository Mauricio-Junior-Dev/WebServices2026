package senac.dws.veiculos.idempotency;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.IdempotencyKey;
import senac.dws.veiculos.exceptions.ConflictException;
import senac.dws.veiculos.repositories.IdempotencyKeyRepository;

import java.time.LocalDateTime;

/**
 * Serviço didático: grava a chave só se ainda não existir.
 * Usamos {@link IdempotencyKeyRepository#existsById(Object)} porque {@code save()} do JPA faria
 * <em>merge</em> numa PK já existente (atualizaria {@code createdAt}) em vez de disparar erro de duplicata.
 * Você pode chamar {@link #registerIfAbsent(String)} também no início de um método de controller.
 */
@Service
public class IdempotencyKeyService {

    private final IdempotencyKeyRepository repository;

    public IdempotencyKeyService(IdempotencyKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void registerIfAbsent(String idempotencyKey) {
        if (repository.existsById(idempotencyKey)) {
            throw new ConflictException("Esta operação já foi processada ou está em andamento");
        }
        try {
            repository.save(new IdempotencyKey(idempotencyKey, LocalDateTime.now()));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Esta operação já foi processada ou está em andamento");
        }
    }
}
