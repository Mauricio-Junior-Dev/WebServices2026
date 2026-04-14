package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.api.ApiErrorResponse;
import senac.dws.veiculos.entities.Engine;
import senac.dws.veiculos.hateoas.EngineModelAssembler;
import senac.dws.veiculos.services.EngineService;

@Tag(name = "Engine", description = "Operações para cadastro de motores (cilindrada, potência) e tipo de combustível.")
@RestController
@RequestMapping("/engines")
public class EngineController {

    private final EngineService engineService;
    private final EngineModelAssembler assembler;

    public EngineController(EngineService engineService, EngineModelAssembler assembler) {
        this.engineService = engineService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista motores paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Engine>>> list(Pageable pageable,
                                                                PagedResourcesAssembler<Engine> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(engineService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca motor por id")
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
    public ResponseEntity<EntityModel<Engine>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(engineService.findById(id)));
    }

    @Operation(summary = "Cria motor")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Combustível não encontrado",
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
    public ResponseEntity<EntityModel<Engine>> create(@Valid @RequestBody Engine engine) {
        Engine saved = engineService.create(engine);
        EntityModel<Engine> model = assembler.toModel(engineService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza motor")
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
            description = "Conflito de dados",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Engine>> update(@PathVariable Long id, @Valid @RequestBody Engine engine) {
        Engine saved = engineService.update(id, engine);
        return ResponseEntity.ok(assembler.toModel(engineService.findById(saved.getId())));
    }

    @Operation(summary = "Remove motor")
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
        engineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca motores por tipo (paginado)",
            description = "Parâmetros padrões de paginação: page, size e sort. Retorna 200 com página vazia quando não há resultados.")
    @ApiResponse(responseCode = "200", description = "Página filtrada")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<Engine>>> search(@RequestParam String type, Pageable pageable,
                                                                  PagedResourcesAssembler<Engine> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                engineService.searchByType(type, pageable), assembler));
    }
}
