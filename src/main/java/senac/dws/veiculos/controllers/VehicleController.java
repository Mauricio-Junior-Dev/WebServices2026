package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.exceptions.VehicleException;
import senac.dws.veiculos.repositories.VehicleRepository;

import java.util.List;

@Tag(name = "Vehicles", description = "API de gerenciamento de veículos")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    //LISTAR
    @Operation(summary = "Lista de todos os veiculos cadastrados")
    @GetMapping
    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }

    //BUSCAR POR ID
    @Operation(summary = "Busca um veiculo pelo ID")
    @GetMapping("/{id}")
    public Vehicle getVehicle(@PathVariable long id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new VehicleException("Veiculo não encontrado"));
    }

    // CRIAR
    @Operation(summary = "Cria um novo veiculo")
    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // ATUALIZAR
    @Operation(summary = "Atualiza um veiculo existente pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle updatedVehicle) {

        return vehicleRepository.findById(id).map(vehicle -> {
            vehicle.setName(updatedVehicle.getName());
            vehicle.setYear(updatedVehicle.getYear());
            vehicle.setPrice(updatedVehicle.getPrice());
            vehicle.setBrand(updatedVehicle.getBrand());
            vehicle.setCategory(updatedVehicle.getCategory());
            vehicle.setEngine(updatedVehicle.getEngine());

            return ResponseEntity.ok(vehicleRepository.save(vehicle));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETAR
    @Operation(summary = "Deleta um veiculo existente pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        var vehicle = vehicleRepository.findById(id).orElse(null);

        if (vehicle == null) {
            return ResponseEntity.notFound().build();
        }

        vehicleRepository.delete(vehicle);
        return ResponseEntity.ok().build();
    }

    //BUSCA POR NOME
    @Operation(summary = "Busca veiculos pelo nome, utilizando busca parcial e ignorando maiúsculas/minúsculas")
    @GetMapping("/search")
    public List<Vehicle> searchByName(@RequestParam String name) {
        return vehicleRepository.findByNameContainingIgnoreCase(name);
    }

    //BUSCA POR ANO
    @Operation(summary = "Busca veiculos pelo ano de fabricação")
    @GetMapping("/year/{year}")
    public List<Vehicle> getByYear(@PathVariable Integer year) {
        return vehicleRepository.findByYear(year);
    }

    //BUSCA POR MARCAR
    @Operation(summary = "Busca veiculos pela marca, utilizando o ID da marca")
    @GetMapping("/brand/{id}")
    public List<Vehicle> getByBrand(@PathVariable Long id) {
        return vehicleRepository.findByBrandId(id);
    }
}
