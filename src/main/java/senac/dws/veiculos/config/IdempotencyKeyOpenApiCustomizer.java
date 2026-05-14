package senac.dws.veiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.idempotency.IdempotencyKeyInterceptor;

import java.util.List;

/**
 * Documenta no OpenAPI o cabeçalho {@link IdempotencyKeyInterceptor#HEADER_NAME} para POST sob {@code /api/**},
 * alinhado ao registo do interceptor em {@link WebMvcConfig}.
 */
@Component
public class IdempotencyKeyOpenApiCustomizer implements OpenApiCustomizer {

    private static final String IDEMPOTENCY_DESCRIPTION = """
            Chave opcional para evitar o processamento duplicado do mesmo POST. \
            Use um valor único por operação lógica (por exemplo, um UUID). \
            Tamanho máximo: 128 caracteres; acima disso a API responde 400. \
            Reutilizar a mesma chave num segundo POST resulta em 409 (conflito).""";

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        for (var entry : openApi.getPaths().entrySet()) {
            String path = entry.getKey();
            if (path == null || !path.startsWith("/api/")) {
                continue;
            }
            PathItem pathItem = entry.getValue();
            if (pathItem == null || pathItem.getPost() == null) {
                continue;
            }
            Operation post = pathItem.getPost();
            if (hasIdempotencyKeyParameter(post)) {
                continue;
            }
            post.addParametersItem(idempotencyKeyParameter());
        }
    }

    private static boolean hasIdempotencyKeyParameter(Operation post) {
        List<Parameter> parameters = post.getParameters();
        if (parameters == null) {
            return false;
        }
        for (Parameter p : parameters) {
            if (p == null) {
                continue;
            }
            if ("header".equalsIgnoreCase(p.getIn())
                    && IdempotencyKeyInterceptor.HEADER_NAME.equalsIgnoreCase(p.getName())) {
                return true;
            }
        }
        return false;
    }

    private static Parameter idempotencyKeyParameter() {
        return new Parameter()
                .in("header")
                .name(IdempotencyKeyInterceptor.HEADER_NAME)
                .description(IDEMPOTENCY_DESCRIPTION.trim())
                .required(false)
                .schema(new StringSchema().maxLength(128));
    }
}
