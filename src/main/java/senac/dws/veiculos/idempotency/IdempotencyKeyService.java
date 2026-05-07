package senac.dws.veiculos.idempotency;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.IdempotencyKey;
import senac.dws.veiculos.exceptions.ConflictException;

import java.time.LocalDateTime;

/**
 * Registra a chave com um INSERT. A coluna {@code key} é PK única no banco:
 * se a chave já existir, o banco (via JPA na {@code flush}) gera erro de integridade
 * e convertemos em HTTP 409.
 * <p>
 * Usamos {@link EntityManager#persist} em vez de {@code repository.save}, porque com
 * identificador já preenchido o {@code save} do Spring Data pode fazer {@code merge}
 * (atualizar) em vez de inserir — e aí não haveria violação de unicidade.
 * <p>
 * Nos controllers, você pode chamar {@link #tryRegisterKey(String)} no início do método POST.
 */
@Service
public class IdempotencyKeyService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void tryRegisterKey(String idempotencyKey) {
        try {
            entityManager.persist(new IdempotencyKey(idempotencyKey, LocalDateTime.now()));
            entityManager.flush();
        } catch (RuntimeException e) {
            if (isDuplicateKey(e)) {
                throw new ConflictException("Esta operação já foi processada ou está em andamento");
            }
            throw e;
        }
    }

    /** Spring pode embrulhar o erro do banco; o Hibernate também pode lançar {@link ConstraintViolationException} direto. */
    private static boolean isDuplicateKey(Throwable ex) {
        while (ex != null) {
            if (ex instanceof DataIntegrityViolationException) {
                return true;
            }
            if (ex instanceof ConstraintViolationException cve) {
                var sql = cve.getSQLException();
                /* 23505 = violação de unicidade / PK duplicada (padrão SQL, usado pelo H2 e outros). */
                return sql != null && "23505".equals(sql.getSQLState());
            }
            ex = ex.getCause();
        }
        return false;
    }
}
