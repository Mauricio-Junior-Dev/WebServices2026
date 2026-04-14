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
import senac.dws.veiculos.entities.FuelType;
import senac.dws.veiculos.hateoas.FuelTypeModelAssembler;
import senac.dws.veiculos.services.FuelTypeService;

@Tag(name = "FuelType", description = "Operações para tipos de combustível referenciados pelos motores.")
@RestController
@RequestMapping("/fuel-types")
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;
    private final FuelTypeModelAssembler assembler;

    public FuelTypeController(FuelTypeService fuelTypeService, FuelTypeModelAssembler assembler) {
        this.fuelTypeService = fuelTypeService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista tipos de combustível paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<FuelType>>> list(Pageable pageable,
                                                                  PagedResourcesAssembler<FuelType> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(fuelTypeService.findAll(pageable), assembler));
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
    public ResponseEntity<EntityModel<FuelType>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(fuelTypeService.findById(id)));
    }

    @Operation(summary = "Cria tipo")
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
    public ResponseEntity<EntityModel<FuelType>> create(@Valid @RequestBody FuelType fuelType) {
        FuelType saved = fuelTypeService.create(fuelType);
        EntityModel<FuelType> model = assembler.toModel(fuelTypeService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza tipo")
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
    public ResponseEntity<EntityModel<FuelType>> update(@PathVariable Long id, @Valid @RequestBody FuelType fuelType) {
        FuelType saved = fuelTypeService.update(id, fuelType);
        return ResponseEntity.ok(assembler.toModel(fuelTypeService.findById(saved.getId())));
    }

    @Operation(summary = "Remove tipo")
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
        fuelTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca por nome (paginado)",
            description = "Parâmetros padrões de paginação: page, size e sort. Retorna 200 com página vazia quando não há resultados.")
    @ApiResponse(responseCode = "200", description = "Página filtrada")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<FuelType>>> search(@RequestParam String name, Pageable pageable,
                                                                    PagedResourcesAssembler<FuelType> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                fuelTypeService.searchByName(name, pageable), assembler));
    }
}
