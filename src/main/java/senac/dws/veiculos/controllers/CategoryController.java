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
import senac.dws.veiculos.entities.Category;
import senac.dws.veiculos.hateoas.CategoryModelAssembler;
import senac.dws.veiculos.services.CategoryService;

@Tag(name = "Category", description = "Operações sobre categorias de veículo (classificação comercial/técnica).")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;

    public CategoryController(CategoryService categoryService, CategoryModelAssembler assembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista categorias paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Category>>> list(Pageable pageable,
                                                                    PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categoryService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca categoria por id")
    @ApiResponse(responseCode = "200", description = "Encontrada")
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(categoryService.findById(id)));
    }

    @Operation(summary = "Cria categoria")
    @ApiResponse(responseCode = "201", description = "Criada")
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
    public ResponseEntity<EntityModel<Category>> create(@Valid @RequestBody Category category) {
        Category saved = categoryService.create(category);
        EntityModel<Category> model = assembler.toModel(categoryService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza categoria")
    @ApiResponse(responseCode = "200", description = "Atualizada")
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrada",
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
    public ResponseEntity<EntityModel<Category>> update(@PathVariable Long id, @Valid @RequestBody Category category) {
        Category saved = categoryService.update(id, category);
        return ResponseEntity.ok(assembler.toModel(categoryService.findById(saved.getId())));
    }

    @Operation(summary = "Remove categoria")
    @ApiResponse(responseCode = "204", description = "Removida")
    @ApiResponse(
            responseCode = "404",
            description = "Não encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca categorias por nome (paginado)",
            description = "Parâmetros padrões de paginação: page, size e sort. Retorna 200 com página vazia quando não há resultados.")
    @ApiResponse(responseCode = "200", description = "Página filtrada")
    @ApiResponse(
            responseCode = "500",
            description = "Erro interno inesperado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<Category>>> search(@RequestParam String name, Pageable pageable,
                                                                    PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                categoryService.searchByName(name, pageable), assembler));
    }
}
