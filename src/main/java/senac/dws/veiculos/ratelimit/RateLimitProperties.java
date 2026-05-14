package senac.dws.veiculos.ratelimit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuração externa do limite de taxa. Ajusta em {@code application.properties} sem recompilar.
 * <p><strong>O que isto significa:</strong> cada cliente (por defeito identificado pelo IP) tem um
 * "crédito" de pedidos por janela de tempo. Cada pedido HTTP conta 1. A cada {@link #refillPeriod}
 * a contagem da janela volta a zero (algoritmo de <em>janela fixa</em>, fácil de explicar na oral).</p>
 */
@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    /** Desliga o filtro (útil em testes locais ou ambientes fechados). */
    private boolean enabled = true;

    /** Máximo de pedidos permitidos por cliente em cada ciclo de {@link #refillPeriod} (tamanho do balde). */
    @Positive
    private int capacity = 120;

    /**
     * Intervalo em que o balde volta a ter {@link #capacity} fichas (ex.: {@code PT1M} = um minuto).
     * Formato ISO-8601 do {@link Duration}.
     */
    @NotNull
    private Duration refillPeriod = Duration.ofMinutes(1);

    /**
     * Se {@code true}, o IP do cliente é o primeiro valor de {@code X-Forwarded-For} (útil atrás de
     * Nginx, Kubernetes ingress, etc.). Só atives se a infraestrutura <strong>substituir</strong> esse
     * cabeçalho de forma confiável — caso contrário um cliente pode forjar outro IP.
     */
    private boolean trustForwardedHeaders = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Duration getRefillPeriod() {
        return refillPeriod;
    }

    public void setRefillPeriod(Duration refillPeriod) {
        this.refillPeriod = refillPeriod;
    }

    public boolean isTrustForwardedHeaders() {
        return trustForwardedHeaders;
    }

    public void setTrustForwardedHeaders(boolean trustForwardedHeaders) {
        this.trustForwardedHeaders = trustForwardedHeaders;
    }
}
