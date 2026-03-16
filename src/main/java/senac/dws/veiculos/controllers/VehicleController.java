package senac.dws.veiculos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.repositories.VehicleRepository;

import java.util.List;

@RestController("/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/vehicles/{id}")
    public Vehicle getVehicle(@PathVariable long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @PostMapping("/vehicles")
    public Vehicle createVehicle(@PathVariable long id) {
        return vehicleRepository.save(Vehicle);
    }

    @DeleteMapping("/vehicles")
    public ResponseEntity deleteVehicle(@PathVariable long id) {
        var vehicle = vehicleRepository.findById(id).orElse( null);
        if (vehicle == null) {
            return ResponseEntity.notFound().build();
        }
        vehicleRepository.delete(vehicle);
        return ResponseEntity.ok().build();
    }
}
