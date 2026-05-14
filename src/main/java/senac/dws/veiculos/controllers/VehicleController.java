package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.api.ApiErrorResponse;
import senac.dws.veiculos.api.ApiVersionHeaders;
import senac.dws.veiculos.api.v2.dto.VehicleListItemV2Dto;
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.exceptions.InvalidRequestException;
import senac.dws.veiculos.hateoas.VehicleModelAssembler;
import senac.dws.veiculos.services.VehicleService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Veículos sob {@code /api/vehicles}. A listagem ({@code GET} na raiz do recurso) admite duas
 * versões de contrato via cabeçalho {@link ApiVersionHeaders#NAME} — ver os dois métodos
 * {@code list...} abaixo.
 */
@Tag(name = "Vehicle", description = "CRUD e buscas de veículos. Na listagem paginada, use o cabeçalho "
        + ApiVersionHeaders.NAME + " para escolher o formato da resposta (1 = HATEOAS completo, 2 = resumo).")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleModelAssembler assembler;

    public VehicleController(VehicleService vehicleService, VehicleModelAssembler assembler) {
        this.vehicleService = vehicleService;
        this.assembler = assembler;
    }

    /**
     * Listagem “versão 1”: HATEOAS + entidade completa. É o comportamento por omissão quando o cliente
     * não envia {@link ApiVersionHeaders#NAME}, ou quando envia o valor {@link ApiVersionHeaders#V1}.
     * <p>Pedidos com {@code X-API-Version=2} são tratados pelo outro {@code @GetMapping} (Spring escolhe
     * o método mais específico pelo cabeçalho).</p>
     */
    @Operation(
            summary = "Lista veículos (versão 1 — HATEOAS)",
            description = "Cabeçalho opcional `" + ApiVersionHeaders.NAME + "`: omitir ou `"
                    + ApiVersionHeaders.V1 + "`. Parâmetros de paginação: page (base 0), size e sort (ex.: sort=name,asc).")
    @Parameter(
            name = ApiVersionHeaders.NAME,
            in = ParameterIn.HEADER,
            required = false,
            description = "Omitir ou `" + ApiVersionHeaders.V1 + "` para este contrato. Outros valores (exceto `"
                    + ApiVersionHeaders.V2 + "`) geram 400.")
    @ApiResponse(responseCode = "200", description = "Página de veículos (HATEOAS)")
    @ApiResponse(
            responseCode = "400",
            description = "Valor de " + ApiVersionHeaders.NAME + " não suportado nesta listagem",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Vehicle>>> listV1(
            @RequestHeader(value = ApiVersionHeaders.NAME, required = false) String apiVersion,
            Pageable pageable,
            PagedResourcesAssembler<Vehicle> pagedResourcesAssembler) {
        assertSupportedVersionForV1List(apiVersion);
        var page = vehicleService.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    /**
     * Listagem “versão 2”: mesma URL que {@link #listV1}, mas o Spring só encaminha para aqui quando o
     * pedido traz explicitamente {@code X-API-Version=2} (condição {@code headers} no {@code @GetMapping}).
     */
    @Operation(
            summary = "Lista veículos (versão 2 — resumo)",
            description = "Exige o cabeçalho `" + ApiVersionHeaders.NAME + ": " + ApiVersionHeaders.V2
                    + "`. Cada item contém apenas id, name e price.")
    @Parameter(
            name = ApiVersionHeaders.NAME,
            in = ParameterIn.HEADER,
            required = true,
            description = "Deve ser exatamente `" + ApiVersionHeaders.V2 + "` para este contrato.")
    @ApiResponse(responseCode = "200", description = "Página de resumos (Spring Data Page JSON)")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(headers = ApiVersionHeaders.SPRING_HEADER_MATCH_V2)
    public ResponseEntity<Page<VehicleListItemV2Dto>> listV2(Pageable pageable) {
        Page<VehicleListItemV2Dto> page = vehicleService.findAll(pageable).map(VehicleListItemV2Dto::from);
        return ResponseEntity.ok(page);
    }

    /**
     * Garante que, se o cliente mandar {@link ApiVersionHeaders#NAME}, só aceitamos “1” neste método
     * (o “2” nunca chega aqui). Qualquer outro valor → 400 com mensagem clara.
     */
    private static void assertSupportedVersionForV1List(String apiVersion) {
        if (apiVersion == null || apiVersion.isBlank()) {
            return;
        }
        String v = apiVersion.trim();
        if (ApiVersionHeaders.V1.equals(v)) {
            return;
        }
        throw new InvalidRequestException(
                ApiVersionHeaders.NAME + " inválida para esta listagem: use \""
                        + ApiVersionHeaders.V1 + "\" ou \""
                        + ApiVersionHeaders.V2 + "\".");
    }

    @Operation(summary = "Busca veículo por id")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> getById(@PathVariable long id) {
        Vehicle v = vehicleService.findById(id);
        return ResponseEntity.ok(assembler.toModel(v));
    }

    @Operation(summary = "Cria veículo")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso")
    @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida (campos obrigatórios ausentes, enum inválido ou JSON malformado)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Recurso relacionado não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Conflito: Já existe um registro com este nome/identificador",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<EntityModel<Vehicle>> create(
            @RequestBody(
                    description = "Payload para criação de veículo",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Vehicle.class),
                            examples = @ExampleObject(
                                    name = "ExemploCreateVehicle",
                                    value = """
                                            {
                                              "name": "Corolla XEi 2.0",
                                              "year": 2024,
                                              "price": 164990.00,
                                              "status": "DISPONIVEL",
                                              "brand": {"id": 1},
                                              "category": {"id": 2},
                                              "engine": {"id": 3},
                                              "acessorios": [{"id": 1}, {"id": 2}]
                                            }
                                            """)))
            @Valid @org.springframework.web.bind.annotation.RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.create(vehicle);
        EntityModel<Vehicle> model = assembler.toModel(vehicleService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza veículo")
    @ApiResponse(responseCode = "200", description = "Atualizado com sucesso")
    @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Conflito de dados",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> update(@PathVariable Long id,
                                                       @RequestBody(
                                                               description = "Payload para atualização de veículo",
                                                               required = true,
                                                               content = @Content(
                                                                       mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                       schema = @Schema(implementation = Vehicle.class),
                                                                       examples = @ExampleObject(
                                                                               name = "ExemploUpdateVehicle",
                                                                               value = """
                                                                                       {
                                                                                         "name": "Corolla XEi 2.0",
                                                                                         "year": 2025,
                                                                                         "price": 169990.00,
                                                                                         "status": "RESERVADO",
                                                                                         "brand": {"id": 1},
                                                                                         "category": {"id": 2},
                                                                                         "engine": {"id": 3},
                                                                                         "acessorios": [{"id": 2}]
                                                                                       }
                                                                                       """)))
                                                       @Valid @org.springframework.web.bind.annotation.RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.update(id, vehicle);
        return ResponseEntity.ok(assembler.toModel(vehicleService.findById(saved.getId())));
    }

    @Operation(summary = "Remove veículo")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca por nome (lista, sem paginação)",
            description = "Retorna 200 com lista vazia quando não há correspondências.")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Vehicle>>> searchByName(@RequestParam String name) {
        var list = vehicleService.searchByName(name).stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<EntityModel<Vehicle>> cm = CollectionModel.of(list);
        cm.add(linkTo(VehicleController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }

    @Operation(summary = "Busca por ano",
            description = "Retorna 200 com lista vazia quando não há veículos para o ano informado.")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/year/{year}")
    public ResponseEntity<CollectionModel<EntityModel<Vehicle>>> getByYear(@PathVariable Integer year) {
        var list = vehicleService.findByYear(year).stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<EntityModel<Vehicle>> cm = CollectionModel.of(list);
        cm.add(linkTo(VehicleController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }

    @Operation(summary = "Busca por marca",
            description = "Retorna 200 com lista vazia quando a marca não possui veículos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/brand/{id}")
    public ResponseEntity<CollectionModel<EntityModel<Vehicle>>> getByBrand(@PathVariable Long id) {
        var list = vehicleService.findByBrandId(id).stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<EntityModel<Vehicle>> cm = CollectionModel.of(list);
        cm.add(linkTo(VehicleController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }
}
