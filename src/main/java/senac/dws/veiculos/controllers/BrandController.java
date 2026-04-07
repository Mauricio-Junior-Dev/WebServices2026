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
import senac.dws.veiculos.entities.Brand;
import senac.dws.veiculos.hateoas.BrandModelAssembler;
import senac.dws.veiculos.services.BrandService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Brand", description = "Operações para cadastro e consulta de marcas, vinculadas ao país de origem.")
@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final BrandModelAssembler assembler;

    public BrandController(BrandService brandService, BrandModelAssembler assembler) {
        this.brandService = brandService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista marcas paginado")
    @ApiResponse(responseCode = "200", description = "Página de marcas")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Brand>>> list(Pageable pageable,
                                                                PagedResourcesAssembler<Brand> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(brandService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca marca por id")
    @ApiResponse(responseCode = "200", description = "Encontrada")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Brand>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(brandService.findById(id)));
    }

    @Operation(summary = "Cria marca")
    @ApiResponse(responseCode = "201", description = "Criada")
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "404", description = "País não encontrado")
    @ApiResponse(
            responseCode = "409",
            description = "Conflito: Já existe um registro com este nome/identificador",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<EntityModel<Brand>> create(@Valid @RequestBody Brand brand) {
        Brand saved = brandService.create(brand);
        EntityModel<Brand> model = assembler.toModel(brandService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza marca")
    @ApiResponse(responseCode = "200", description = "Atualizada")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Brand>> update(@PathVariable Long id, @Valid @RequestBody Brand brand) {
        Brand saved = brandService.update(id, brand);
        return ResponseEntity.ok(assembler.toModel(brandService.findById(saved.getId())));
    }

    @Operation(summary = "Remove marca")
    @ApiResponse(responseCode = "204", description = "Removida")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca marcas por nome")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Brand>>> search(@RequestParam String name) {
        var list = brandService.searchByName(name).stream().map(assembler::toModel).toList();
        CollectionModel<EntityModel<Brand>> cm = CollectionModel.of(list);
        cm.add(linkTo(BrandController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }
}
