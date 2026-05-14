package senac.dws.veiculos.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><strong>O que faz:</strong> antes do Spring MVC (e antes dos interceptores), verifica se o
 * cliente ainda tem "crédito" de pedidos. Se não tiver, responde {@code 429 Too Many Requests} com
 * cabeçalhos {@code Retry-After} e {@code X-RateLimit-*}, no estilo usado por GitHub, Stripe e
 * outras APIs públicas.</p>
 *
 * <p><strong>Diferença da idempotência (409):</strong> aqui limitamos <em>volume</em> (pedidos por
 * intervalo por IP). A idempotência limita <em>repetição da mesma operação</em> (mesma
 * {@code Idempotency-Key}).</p>
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final ObjectMapper JSON = createJsonMapper();

    private static ObjectMapper createJsonMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return m;
    }

    /** Máximo de pedidos por janela (espelha a configuração; enviado em cada resposta). */
    public static final String HEADER_LIMIT = "X-RateLimit-Limit";

    /** Pedidos ainda permitidos na janela atual após este pedido (ou 0 se o pedido foi bloqueado). */
    public static final String HEADER_REMAINING = "X-RateLimit-Remaining";

    /** Instante UTC (epoch segundos) em que a quota da janela atual renova por completo. */
    public static final String HEADER_RESET = "X-RateLimit-Reset";

    /** Segundos que o cliente deve esperar antes de voltar a tentar (comum com 429). */
    public static final String HEADER_RETRY_AFTER = "Retry-After";

    private final RateLimitProperties properties;
    private final RateLimitBucketManager bucketManager;

    public RateLimitingFilter(RateLimitProperties properties, RateLimitBucketManager bucketManager) {
        this.properties = properties;
        this.bucketManager = bucketManager;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String context = request.getContextPath() == null ? "" : request.getContextPath();
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith(context + "/api");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientId = ClientIdResolver.resolve(request, properties);
        RateLimitDecision decision = bucketManager.tryConsumeOne(clientId);

        if (!decision.consumed()) {
            long retryAfterSeconds = ceilDiv(decision.nanosToWaitForRetry(), 1_000_000_000L);
            final int status429 = HttpStatus.TOO_MANY_REQUESTS.value();

            response.setStatus(status429);
            response.setHeader(HEADER_LIMIT, Integer.toString(properties.getCapacity()));
            response.setHeader(HEADER_REMAINING, "0");
            response.setHeader(HEADER_RESET, Long.toString(decision.resetEpochSecond()));
            response.setHeader(HEADER_RETRY_AFTER, Long.toString(retryAfterSeconds));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", status429);
            body.put("error", "Too Many Requests");
            body.put("message", "Limite de pedidos excedido para este cliente. Tente novamente após "
                    + retryAfterSeconds + " segundos.");

            JSON.writeValue(response.getOutputStream(), body);
            return;
        }

        response.setHeader(HEADER_LIMIT, Integer.toString(properties.getCapacity()));
        response.setHeader(HEADER_REMAINING, Long.toString(decision.remainingAfterRequest()));
        response.setHeader(HEADER_RESET, Long.toString(decision.resetEpochSecond()));

        filterChain.doFilter(request, response);
    }

    private static long ceilDiv(long a, long b) {
        return (a + b - 1) / b;
    }
}
