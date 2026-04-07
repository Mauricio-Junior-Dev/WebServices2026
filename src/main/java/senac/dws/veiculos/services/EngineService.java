package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Engine;
import senac.dws.veiculos.entities.FuelType;
import senac.dws.veiculos.exceptions.EngineException;
import senac.dws.veiculos.exceptions.FuelTypeException;
import senac.dws.veiculos.exceptions.InvalidRequestException;
import senac.dws.veiculos.repositories.EngineRepository;
import senac.dws.veiculos.repositories.FuelTypeRepository;

@Service
public class EngineService {

    private final EngineRepository engineRepository;
    private final FuelTypeRepository fuelTypeRepository;

    public EngineService(EngineRepository engineRepository, FuelTypeRepository fuelTypeRepository) {
        this.engineRepository = engineRepository;
        this.fuelTypeRepository = fuelTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<Engine> findAll(Pageable pageable) {
        return engineRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Engine findById(Long id) {
        return engineRepository.findById(id)
                .orElseThrow(() -> new EngineException("Motor não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<Engine> searchByType(String type, Pageable pageable) {
        return engineRepository.findByTypeContainingIgnoreCase(type, pageable);
    }

    public Engine create(Engine engine) {
        engine.setFuelType(resolveFuelType(engine.getFuelType()));
        return engineRepository.save(engine);
    }

    public Engine update(Long id, Engine input) {
        Engine engine = findById(id);
        engine.setType(input.getType());
        engine.setDisplacement(input.getDisplacement());
        engine.setHorsepower(input.getHorsepower());
        engine.setFuelType(resolveFuelType(input.getFuelType()));
        return engineRepository.save(engine);
    }

    public void deleteById(Long id) {
        Engine engine = findById(id);
        engineRepository.delete(engine);
    }

    private FuelType resolveFuelType(FuelType ref) {
        if (ref == null || ref.getId() == null) {
            throw new InvalidRequestException("Tipo de combustível (id) é obrigatório");
        }
        return fuelTypeRepository.findById(ref.getId())
                .orElseThrow(() -> new FuelTypeException("Tipo de combustível não encontrado"));
    }
}
