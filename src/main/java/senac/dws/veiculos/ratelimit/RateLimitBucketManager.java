package senac.dws.veiculos.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Conta pedidos por cliente usando uma <strong>janela fixa</strong> no tempo (sem bibliotecas externas).
 * <p><strong>Ideia simples para apresentar:</strong> o tempo é dividido em segmentos de duração
 * {@link RateLimitProperties#getRefillPeriod()}. Dentro de cada segmento, o cliente pode fazer no
 * máximo {@link RateLimitProperties#getCapacity()} pedidos. Ao mudar de segmento, o contador volta a zero.</p>
 * <p>Em produção com várias instâncias, cada JVM teria o seu mapa — para quota global costuma-se Redis
 * ou um API Gateway.</p>
 */
@Component
public class RateLimitBucketManager {

    private final RateLimitProperties properties;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ClientWindow> windows = new ConcurrentHashMap<>();

    public RateLimitBucketManager(RateLimitProperties properties) {
        this.properties = properties;
    }

    /**
     * Tenta registar um pedido para o cliente. Thread-safe por cliente (um cadeado por {@code clientId}).
     */
    public RateLimitDecision tryConsumeOne(String clientId) {
        long periodMillis = effectivePeriodMillis();
        Object lock = locks.computeIfAbsent(clientId, k -> new Object());
        synchronized (lock) {
            long nowMillis = System.currentTimeMillis();
            long windowId = nowMillis / periodMillis;
            long nextWindowStartMillis = (windowId + 1) * periodMillis;
            long resetEpochSecond = nextWindowStartMillis / 1000;

            ClientWindow state = windows.computeIfAbsent(clientId, k -> new ClientWindow());
            if (state.windowId != windowId) {
                state.windowId = windowId;
                state.used = 0;
            }

            int capacity = properties.getCapacity();
            if (state.used >= capacity) {
                long waitMillis = Math.max(1L, nextWindowStartMillis - nowMillis);
                return new RateLimitDecision(false, 0, waitMillis * 1_000_000L, resetEpochSecond);
            }

            state.used++;
            long remaining = (long) capacity - state.used;
            return new RateLimitDecision(true, remaining, 0L, resetEpochSecond);
        }
    }

    /** Evita janela 0 ms; suporta períodos sub-segundo nos testes. */
    private long effectivePeriodMillis() {
        long ms = properties.getRefillPeriod().toMillis();
        return Math.max(1L, ms);
    }

    private static final class ClientWindow {
        private long windowId;
        private int used;
    }
}
