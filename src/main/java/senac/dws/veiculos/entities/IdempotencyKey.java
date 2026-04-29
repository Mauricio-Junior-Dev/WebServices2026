package senac.dws.veiculos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Registro mínimo de chave de idempotência enviada pelo cliente (cabeçalho Idempotency-Key).
 * A própria PK garante que a mesma chave não seja aceita duas vezes.
 */
@Entity
@Table(name = "idempotency_key")
public class IdempotencyKey {

    @Id
    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String key;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public IdempotencyKey() {
    }

    public IdempotencyKey(String key, LocalDateTime createdAt) {
        this.key = key;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
