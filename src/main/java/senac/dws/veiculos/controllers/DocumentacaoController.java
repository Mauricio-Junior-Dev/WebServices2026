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
import senac.dws.veiculos.entities.Documentacao;
import senac.dws.veiculos.hateoas.DocumentacaoModelAssembler;
import senac.dws.veiculos.services.DocumentacaoService;

@Tag(name = "Documentacao", description = "Operações para documentação regulatória ligada a um único veículo (1:1).")
@RestController
@RequestMapping("/documentacoes")
public class DocumentacaoController {

    private final DocumentacaoService documentacaoService;
    private final DocumentacaoModelAssembler assembler;

    public DocumentacaoController(DocumentacaoService documentacaoService, DocumentacaoModelAssembler assembler) {
        this.documentacaoService = documentacaoService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista documentações paginado")
    @ApiResponse(responseCode = "200", description = "Página")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Documentacao>>> list(Pageable pageable,
                                                                      PagedResourcesAssembler<Documentacao> pagedResourcesAssembler) {
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(documentacaoService.findAll(pageable), assembler));
    }

    @Operation(summary = "Busca por id")
    @ApiResponse(responseCode = "200", description = "Encontrada")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Documentacao>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(documentacaoService.findById(id)));
    }

    @Operation(summary = "Cria documentação vinculada a um veículo")
    @ApiResponse(responseCode = "201", description = "Criada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    @ApiResponse(
            responseCode = "409",
            description = "Conflito: Já existe um registro com este nome/identificador (número de registro duplicado ou veículo já possui documentação)",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<EntityModel<Documentacao>> create(@Valid @RequestBody Documentacao documentacao) {
        Documentacao saved = documentacaoService.create(documentacao);
        EntityModel<Documentacao> model = assembler.toModel(documentacaoService.findById(saved.getId()));
        return ResponseEntity.status(201)
                .location(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Operation(summary = "Atualiza documentação")
    @ApiResponse(responseCode = "200", description = "Atualizada")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @ApiResponse(responseCode = "409", description = "Número de registro em conflito")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Documentacao>> update(@PathVariable Long id,
                                                            @Valid @RequestBody Documentacao documentacao) {
        Documentacao saved = documentacaoService.update(id, documentacao);
        return ResponseEntity.ok(assembler.toModel(documentacaoService.findById(saved.getId())));
    }

    @Operation(summary = "Remove documentação")
    @ApiResponse(responseCode = "204", description = "Removida")
    @ApiResponse(responseCode = "404", description = "Não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentacaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
