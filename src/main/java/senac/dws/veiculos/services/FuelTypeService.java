package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.FuelType;
import senac.dws.veiculos.exceptions.FuelTypeException;
import senac.dws.veiculos.repositories.FuelTypeRepository;

@Service
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    public FuelTypeService(FuelTypeRepository fuelTypeRepository) {
        this.fuelTypeRepository = fuelTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<FuelType> findAll(Pageable pageable) {
        return fuelTypeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public FuelType findById(Long id) {
        return fuelTypeRepository.findById(id)
                .orElseThrow(() -> new FuelTypeException("Tipo de combustível não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<FuelType> searchByName(String name, Pageable pageable) {
        return fuelTypeRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public FuelType create(FuelType fuelType) {
        return fuelTypeRepository.save(fuelType);
    }

    public FuelType update(Long id, FuelType input) {
        FuelType fuelType = findById(id);
        fuelType.setName(input.getName());
        return fuelTypeRepository.save(fuelType);
    }

    public void deleteById(Long id) {
        FuelType fuelType = findById(id);
        fuelTypeRepository.delete(fuelType);
    }
}
