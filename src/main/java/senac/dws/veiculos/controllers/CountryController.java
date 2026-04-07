package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.Country;
import senac.dws.veiculos.hateoas.CountryModelAssembler;
import senac.dws.veiculos.services.CountryService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Países", description = "CRUD de países")
@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryService countryService;
    private final CountryModelAssembler assembler;

    public CountryController(CountryService countryService, CountryModelAssembler assembler) {
        this.countryService = countryService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista países paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Country>>> list(Pageable pageable,
                                                                   PagedResourcesAssembler<Country> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(countryService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca país por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Country>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(countryService.findById(id)));
    }

    @Operation(summary = "Cria país")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Country>> create(@Valid @RequestBody Country country) {
        Country saved = countryService.create(country);
        EntityModel<Country> model = assembler.toModel(countryService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza país")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @ApiResponse(responseCode = "409", description = "Conflito de nome")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Country>> update(@PathVariable Long id, @Valid @RequestBody Country country) {
        Country saved = countryService.update(id, country);
        return ResponseEntity.ok(assembler.toModel(countryService.findById(saved.getId())));
    }

    @Operation(summary = "Remove país")
    @ApiResponse(responseCode = "204", description = "Removido")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        countryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca países por nome")
    @ApiResponse(responseCode = "200", description = "Resultados")
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Country>>> search(@RequestParam String name) {
        var list = countryService.searchByName(name).stream().map(assembler::toModel).toList();
        CollectionModel<EntityModel<Country>> cm = CollectionModel.of(list);
        cm.add(linkTo(CountryController.class).withRel("collection"));
        return ResponseEntity.ok(cm);
    }
}
