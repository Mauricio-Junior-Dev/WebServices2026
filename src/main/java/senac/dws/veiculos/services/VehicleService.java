package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.*;
import senac.dws.veiculos.exceptions.AcessorioException;
import senac.dws.veiculos.exceptions.BrandException;
import senac.dws.veiculos.exceptions.CategoryException;
import senac.dws.veiculos.exceptions.EngineException;
import senac.dws.veiculos.exceptions.InvalidRequestException;
import senac.dws.veiculos.exceptions.VehicleException;
import senac.dws.veiculos.repositories.*;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final EngineRepository engineRepository;
    private final AcessorioRepository acessorioRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          BrandRepository brandRepository,
                          CategoryRepository categoryRepository,
                          EngineRepository engineRepository,
                          AcessorioRepository acessorioRepository) {
        this.vehicleRepository = vehicleRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.engineRepository = engineRepository;
        this.acessorioRepository = acessorioRepository;
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return loadDetail(id);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> searchByName(String name) {
        return vehicleRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByYear(Integer year) {
        return vehicleRepository.findByYear(year);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByBrandId(Long brandId) {
        return vehicleRepository.findByBrandId(brandId);
    }

    public Vehicle create(Vehicle input) {
        Vehicle v = new Vehicle();
        applyScalars(v, input);
        v.setBrand(resolveBrand(input.getBrand()));
        v.setCategory(resolveCategory(input.getCategory()));
        v.setEngine(resolveEngine(input.getEngine()));
        if (input.getDocumentacao() != null) {
            Documentacao d = new Documentacao();
            d.setNumeroRegistro(input.getDocumentacao().getNumeroRegistro());
            d.setObservacao(input.getDocumentacao().getObservacao());
            v.setDocumentacao(d);
        }
        replaceAcessorios(v, input.getAcessorios());
        return vehicleRepository.save(v);
    }

    public Vehicle update(Long id, Vehicle input) {
        Vehicle v = loadDetail(id);
        applyScalars(v, input);
        v.setBrand(resolveBrand(input.getBrand()));
        v.setCategory(resolveCategory(input.getCategory()));
        v.setEngine(resolveEngine(input.getEngine()));

        if (input.getDocumentacao() == null) {
            v.setDocumentacao(null);
        } else {
            if (v.getDocumentacao() == null) {
                Documentacao d = new Documentacao();
                v.setDocumentacao(d);
            }
            v.getDocumentacao().setNumeroRegistro(input.getDocumentacao().getNumeroRegistro());
            v.getDocumentacao().setObservacao(input.getDocumentacao().getObservacao());
        }
        replaceAcessorios(v, input.getAcessorios());
        return vehicleRepository.save(v);
    }

    public void deleteById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleException("Veículo não encontrado"));
        vehicleRepository.delete(v);
    }

    private Vehicle loadDetail(Long id) {
        return vehicleRepository.findDetailById(id)
                .orElseThrow(() -> new VehicleException("Veículo não encontrado"));
    }

    private void applyScalars(Vehicle target, Vehicle source) {
        target.setName(source.getName());
        target.setYear(source.getYear());
        target.setPrice(source.getPrice());
        target.setStatus(source.getStatus());
    }

    private Brand resolveBrand(Brand ref) {
        if (ref == null || ref.getId() == null) {
            throw new InvalidRequestException("Marca (id) é obrigatória");
        }
        return brandRepository.findById(ref.getId())
                .orElseThrow(() -> new BrandException("Marca não encontrada"));
    }

    private Category resolveCategory(Category ref) {
        if (ref == null || ref.getId() == null) {
            throw new InvalidRequestException("Categoria (id) é obrigatória");
        }
        return categoryRepository.findById(ref.getId())
                .orElseThrow(() -> new CategoryException("Categoria não encontrada"));
    }

    private Engine resolveEngine(Engine ref) {
        if (ref == null || ref.getId() == null) {
            throw new InvalidRequestException("Motor (id) é obrigatório");
        }
        return engineRepository.findById(ref.getId())
                .orElseThrow(() -> new EngineException("Motor não encontrado"));
    }

    private void replaceAcessorios(Vehicle vehicle, java.util.Set<Acessorio> refs) {
        vehicle.getAcessorios().clear();
        if (refs == null || refs.isEmpty()) {
            return;
        }
        for (Acessorio ref : refs) {
            if (ref == null || ref.getId() == null) {
                throw new InvalidRequestException("Acessório (id) inválido");
            }
            Acessorio a = acessorioRepository.findById(ref.getId())
                    .orElseThrow(() -> new AcessorioException("Acessório não encontrado"));
            vehicle.getAcessorios().add(a);
        }
    }
}
