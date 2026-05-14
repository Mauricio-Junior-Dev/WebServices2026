package senac.dws.veiculos.ratelimit;

/**
 * Resultado de tentar "gastar" um pedido no limite de taxa.
 *
 * @param consumed               {@code true} se o pedido pode continuar (ainda havia quota na janela).
 * @param remainingAfterRequest  pedidos ainda permitidos na janela atual <em>após</em> contar este (0 se bloqueado).
 * @param nanosToWaitForRetry    se {@code consumed} é {@code false}, tempo até a próxima janela (para {@code Retry-After}).
 * @param resetEpochSecond       instante UTC (segundos desde epoch) em que a quota da janela atual renova por completo.
 */
public record RateLimitDecision(
        boolean consumed,
        long remainingAfterRequest,
        long nanosToWaitForRetry,
        long resetEpochSecond
) {
}
