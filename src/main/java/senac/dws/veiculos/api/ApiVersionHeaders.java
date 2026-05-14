package senac.dws.veiculos.api;

/**
 * <p>Esta API <strong>não</strong> usa versão no URL (não existe {@code /v1} ou {@code /v2} no path).
 * O versionamento é feito pelo cabeçalho HTTP {@value #NAME}, quando o endpoint suporta mais de um contrato.</p>
 *
 * <p><strong>Onde se aplica hoje:</strong> apenas no {@code GET /api/vehicles} (listagem paginada).
 * Os outros recursos vivem num único contrato sob {@code /api/...}.</p>
 *
 * <ul>
 *   <li>{@value #V1} ou cabeçalho ausente → listagem com HATEOAS (formato “completo”).</li>
 *   <li>{@value #V2} → mesma URL, resposta JSON mais simples (só id, name, price por item).</li>
 * </ul>
 */
public final class ApiVersionHeaders {

    /** Nome do cabeçalho exigido pelo enunciado do trabalho. */
    public static final String NAME = "X-API-Version";

    /** Contrato “completo” (HATEOAS + entidade Vehicle). */
    public static final String V1 = "1";

    /** Contrato resumido ({@link senac.dws.veiculos.api.v2.dto.VehicleListItemV2Dto}). */
    public static final String V2 = "2";

    /**
     * Valor usado no {@code @GetMapping(headers = ...)} do Spring MVC para escolher o método
     * que trata a listagem na versão 2.
     */
    public static final String SPRING_HEADER_MATCH_V2 = NAME + "=" + V2;

    private ApiVersionHeaders() {
    }
}
