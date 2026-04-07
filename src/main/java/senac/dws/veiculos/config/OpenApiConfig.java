package senac.dws.veiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
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
                e portais administrativos.""";

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
                                .description("Veículos: estoque, consultas filtradas, vínculos com marca, categoria, motor, documentação e acessórios."),
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
}
