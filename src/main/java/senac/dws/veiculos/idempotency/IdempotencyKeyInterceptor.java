package senac.dws.veiculos.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import senac.dws.veiculos.exceptions.InvalidRequestException;

/**
 * <p><strong>Idempotência (não é rate limiting)</strong> — POST com cabeçalho {@value #HEADER_NAME}
 * tenta gravar a chave na base antes do controller. Chave repetida → {@code 409 Conflict}
 * ({@link senac.dws.veiculos.exceptions.ConflictException}): significa “esta mesma operação lógica
 * já foi aceite”, não “estás a pedir demasiado por segundo”.</p>
 *
 * <p><strong>Rate limiting</strong> (limite por IP/cliente, {@code 429 Too Many Requests},
 * cabeçalhos tipo {@code X-RateLimit-*}, {@code Retry-After}) é outro conceito: protege a infraestrutura
 * contra abuso de volume; deve ser implementado à parte (filtro/interceptor ou API gateway).</p>
 *
 * <p>Sem cabeçalho, o fluxo segue normal (idempotência opcional).</p>
 */
@Component
public class IdempotencyKeyInterceptor implements HandlerInterceptor {

    public static final String HEADER_NAME = "Idempotency-Key";
    private static final int MAX_KEY_LENGTH = 128;

    private final IdempotencyKeyService idempotencyKeyService;

    public IdempotencyKeyInterceptor(IdempotencyKeyService idempotencyKeyService) {
        this.idempotencyKeyService = idempotencyKeyService;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String raw = request.getHeader(HEADER_NAME);
        if (raw == null || raw.isBlank()) {
            return true;
        }

        String key = raw.trim();
        if (key.length() > MAX_KEY_LENGTH) {
            throw new InvalidRequestException("Idempotency-Key excede " + MAX_KEY_LENGTH + " caracteres");
        }

        idempotencyKeyService.tryRegisterKey(key);
        return true;
    }
}
