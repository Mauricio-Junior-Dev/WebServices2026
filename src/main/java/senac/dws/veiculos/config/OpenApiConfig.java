package senac.dws.veiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fleetManagementOpenAPI(
            @Value("${openapi.contact.name}") String contactName,
            @Value("${openapi.contact.email}") String contactEmail) {

        final String description = """
                API REST para gestão de frota automotiva, oferecendo cadastro e consulta de veículos, \
                marcas, categorias, fatores técnicos (motores e combustíveis), acessórios e documentação. \
                As respostas seguem HATEOAS (Spring HATEOAS) e as listas principais suportam paginação \
                e ordenação via parâmetros `page`, `size` e `sort`, facilitando integração com clientes \
                e portais administrativos.

                **Versionamento (cabeçalho `X-API-Version`):** não há segmentos `/v1` ou `/v2` na URL. \
                Na listagem `GET /api/vehicles`, o cliente escolhe o contrato: omitir o cabeçalho ou \
                enviar `1` → página HATEOAS com entidade completa; enviar `2` → página no formato Spring Data \
                com itens resumidos (`id`, `name`, `price`). Outros valores para esse cabeçalho na listagem \
                geram **400 Bad Request**.

                **Idempotência (POST em `/api/**`):** em qualquer POST pode ser enviado o cabeçalho opcional \
                `Idempotency-Key` (string, até 128 caracteres). A mesma chave não pode ser reutilizada; \
                uma segunda tentativa com a mesma chave recebe **409 Conflict**. Chaves acima do limite \
                resultam em **400 Bad Request**.

                **Limite de taxa (`/api/**`):** pedidos são contados por cliente (por defeito o IP da ligação). \
                Quando o limite é excedido a API responde **429 Too Many Requests** com cabeçalhos \
                `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` e `Retry-After` (segundos). \
                Isto é independente da idempotência (429 = volume; 409 = operação duplicada).""";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Frota Automotiva")
                        .version("1.0")
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)))
                .tags(List.of(
                        new Tag().name("Vehicle")
                                .description("Veículos: CRUD, buscas e listagem paginada com "
                                        + "dois contratos via cabeçalho X-API-Version (omitir ou 1 = HATEOAS; 2 = resumo)."),
                        new Tag().name("Brand")
                                .description("Marcas automotivas associadas a países de origem."),
                        new Tag().name("Country")
                                .description("Cadastro de países referenciados pelas marcas."),
                        new Tag().name("Category")
                                .description("Tipos ou categorias de veículo (ex.: SUV, hatchback)."),
                        new Tag().name("Engine")
                                .description("Especificações de motor e ligação com tipo de combustível."),
                        new Tag().name("FuelType")
                                .description("Tipos de combustível usados pelos motores."),
                        new Tag().name("Acessorio")
                                .description("Itens opcionais; relação N:N com veículos."),
                        new Tag().name("Documentacao")
                                .description("Registro documental em relação 1:1 com veículo."),
                        new Tag().name("Demo")
                                .description("Endpoint ilustrativo com HATEOAS básico.")));
    }

    /** Um único documento OpenAPI para tudo o que está sob `/api/**`. */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .displayName("API")
                .pathsToMatch("/api/**")
                .build();
    }
}
