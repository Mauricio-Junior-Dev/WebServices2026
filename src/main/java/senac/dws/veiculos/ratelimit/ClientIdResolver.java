package senac.dws.veiculos.ratelimit;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Descobre <em>quem</em> é o cliente para contar pedidos. Por defeito usamos o IP da ligação TCP
 * ({@link HttpServletRequest#getRemoteAddr()}). Em servidores atrás de proxy reverso, o IP real
 * costuma vir no cabeçalho {@code X-Forwarded-For} — só o usamos se
 * {@link RateLimitProperties#isTrustForwardedHeaders()} estiver {@code true}.
 */
public final class ClientIdResolver {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private ClientIdResolver() {
    }

    public static String resolve(HttpServletRequest request, RateLimitProperties properties) {
        if (properties.isTrustForwardedHeaders()) {
            String forwarded = request.getHeader(X_FORWARDED_FOR);
            if (forwarded != null && !forwarded.isBlank()) {
                /* Lista "cliente, proxy1, proxy2" — o primeiro é o cliente original. */
                int comma = forwarded.indexOf(',');
                String first = comma < 0 ? forwarded : forwarded.substring(0, comma);
                return first.trim();
            }
        }
        String remote = request.getRemoteAddr();
        return remote != null ? remote : "unknown";
    }
}
