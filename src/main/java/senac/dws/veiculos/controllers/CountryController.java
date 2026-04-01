package senac.dws.veiculos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.Country;
import senac.dws.veiculos.exceptions.CountryException;
import senac.dws.veiculos.repositories.CountryRepository;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryRepository repository;

    public CountryController(CountryRepository repository) {
        this.repository = repository;
    }

    // LISTAR TODOS
    @GetMapping
    public List<Country> getAll() {
        return repository.findAll();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public Country getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CountryException("País não encontrado"));
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Country country) {
        if (repository.existsByNameIgnoreCase(country.getName())) {
            return ResponseEntity
                    .unprocessableContent()
                    .body("Pais ja cadastrado");
        }
        Country saved = repository.save(country);
        return ResponseEntity.status(201).body(saved);
    }

    // ATUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Country> update(@PathVariable Long id, @RequestBody Country updated) {

        return repository.findById(id).map(country -> {
            country.setName(updated.getName());
            return ResponseEntity.ok(repository.save(country));
        }).orElseThrow(() -> new CountryException("País não encontrado"));
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        Country country = repository.findById(id)
                .orElseThrow(() -> new CountryException("País não encontrado"));

        repository.delete(country);

        return ResponseEntity.noContent().build();
    }

    // BUSCA POR NOME
    @GetMapping("/search")
    public List<Country> search(@RequestParam String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
}