package senac.dws.veiculos.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.Brand;
import senac.dws.veiculos.exceptions.BrandException;
import senac.dws.veiculos.repositories.BrandRepository;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {
    private final BrandRepository brandRepository;

    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    // LISTAR TODAS AS MARCAS
    @GetMapping
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public Brand getBrandById(@PathVariable Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandException("Marca não encontrada"));
    }

    // CRIAR MARCA
    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        Brand saved = brandRepository.save(brand);
        return ResponseEntity.status(201).body(saved);
    }

    // ATUALIZAR MARCA
    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody Brand updatedBrand) {

        return brandRepository.findById(id).map(brand -> {
            brand.setName(updatedBrand.getName());
            return ResponseEntity.ok(brandRepository.save(brand));
        }).orElseThrow(() -> new BrandException("Marca não encontrada"));
    }

    // DELETAR MARCA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandException("Marca não encontrada"));

        brandRepository.delete(brand);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Brand> searchBrand(@RequestParam String name) {
        return brandRepository.findByNameContainingIgnoreCase(name);
    }
}
