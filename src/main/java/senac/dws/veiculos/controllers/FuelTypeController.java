package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.FuelType;
import senac.dws.veiculos.hateoas.FuelTypeModelAssembler;
import senac.dws.veiculos.services.FuelTypeService;

@Tag(name = "Combustíveis", description = "CRUD de tipos de combustível")
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
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<FuelType>>> list(Pageable pageable,
                                                                  PagedResourcesAssembler<FuelType> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(fuelTypeService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FuelType>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(fuelTypeService.findById(id)));
    }

    @Operation(summary = "Cria tipo")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
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
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<FuelType>> update(@PathVariable Long id, @Valid @RequestBody FuelType fuelType) {
        FuelType saved = fuelTypeService.update(id, fuelType);
        return ResponseEntity.ok(assembler.toModel(fuelTypeService.findById(saved.getId())));
    }

    @Operation(summary = "Remove tipo")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca por nome (paginado)")
    @ApiResponse(responseCode = "200", description = "Página filtrada")
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<FuelType>>> search(@RequestParam String name, Pageable pageable,
                                                                    PagedResourcesAssembler<FuelType> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                fuelTypeService.searchByName(name, pageable), assembler));
    }
}
