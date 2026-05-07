package senac.dws.veiculos.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import senac.dws.veiculos.exceptions.InvalidRequestException;

/**
 * POST + cabeçalho {@value #HEADER_NAME} → tenta registrar a chave no banco antes do controller.
 * Sem cabeçalho, o fluxo segue normal (idempotência opcional).
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
