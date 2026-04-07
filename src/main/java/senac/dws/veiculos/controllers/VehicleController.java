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
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.hateoas.VehicleModelAssembler;
import senac.dws.veiculos.services.VehicleService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Vehicle", description = "Operações para gerenciamento de veículos, estoque e vínculos com marca, categoria, motor, documentação e acessórios.")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleModelAssembler assembler;

    public VehicleController(VehicleService vehicleService, VehicleModelAssembler assembler) {
        this.vehicleService = vehicleService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista veículos com paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Página de veículos")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Vehicle>>> list(Pageable pageable,
                                                                 PagedResourcesAssembler<Vehicle> pagedResourcesAssembler) {
        var page = vehicleService.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Busca veículo por id")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> getById(@PathVariable long id) {
        Vehicle v = vehicleService.findById(id);
        return ResponseEntity.ok(assembler.toModel(v));
    }

    @Operation(summary = "Cria veículo")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "404", description = "Recurso relacionado não encontrado")
    @ApiResponse(
            responseCode = "409",
            description = "Conflito: Já existe um registro com este nome/identificador",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<EntityModel<Vehicle>> create(@Valid @RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.create(vehicle);
        EntityModel<Vehicle> model = assembler.toModel(vehicleService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza veículo")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> update(@PathVariable Long id,
                                                       @Valid @RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.update(id, vehicle);
        return ResponseEntity.ok(assembler.toModel(vehicleService.findById(saved.getId())));
    }

    @Operation(summary = "Remove veículo")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca por nome (lista, sem paginação)")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Vehicle>>> searchByName(@RequestParam String name) {
        var list = vehicleService.searchByName(name).stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<EntityModel<Vehicle>> cm = CollectionModel.of(list);
        cm.add(linkTo(VehicleController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }

    @Operation(summary = "Busca por ano")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @GetMapping("/year/{year}")
    public ResponseEntity<CollectionModel<EntityModel<Vehicle>>> getByYear(@PathVariable Integer year) {
        var list = vehicleService.findByYear(year).stream()
                .map(assembler::toModel)
                .toList();
        CollectionModel<EntityModel<Vehicle>> cm = CollectionModel.of(list);
        cm.add(linkTo(VehicleController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }

    @Operation(summary = "Busca por marca")
    @ApiResponse(responseCode = "200", description = "Resultados")
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
