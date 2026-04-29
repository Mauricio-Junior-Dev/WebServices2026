package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.api.ApiErrorResponse;
import senac.dws.veiculos.entities.Acessorio;
import senac.dws.veiculos.hateoas.AcessorioModelAssembler;
import senac.dws.veiculos.services.AcessorioService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Acessorio", description = "Operações para acessórios opcionais do veículo (associação N:N).")
@RestController
@RequestMapping("/api/v1/acessorios")
public class AcessorioController {

    private final AcessorioService acessorioService;
    private final AcessorioModelAssembler assembler;

    public AcessorioController(AcessorioService acessorioService, AcessorioModelAssembler assembler) {
        this.acessorioService = acessorioService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista acessórios paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Acessorio>>> list(Pageable pageable,
                                                                   PagedResourcesAssembler<Acessorio> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(acessorioService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
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
    public ResponseEntity<EntityModel<Acessorio>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(acessorioService.findById(id)));
    }

    @Operation(summary = "Cria acessório")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
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
    public ResponseEntity<EntityModel<Acessorio>> create(@Valid @RequestBody Acessorio acessorio) {
        Acessorio saved = acessorioService.create(acessorio);
        EntityModel<Acessorio> model = assembler.toModel(acessorioService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza acessório")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Conflito de nome",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Acessorio>> update(@PathVariable Long id, @Valid @RequestBody Acessorio acessorio) {
        Acessorio saved = acessorioService.update(id, acessorio);
        return ResponseEntity.ok(assembler.toModel(acessorioService.findById(saved.getId())));
    }

    @Operation(summary = "Remove acessório")
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
        acessorioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca por nome (lista)",
            description = "Retorna 200 com lista vazia quando não há correspondências.")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Acessorio>>> search(@RequestParam String nome) {
        var list = acessorioService.searchByNome(nome).stream().map(assembler::toModel).toList();
        CollectionModel<EntityModel<Acessorio>> cm = CollectionModel.of(list);
        cm.add(linkTo(AcessorioController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }
}
