package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Brand;
import senac.dws.veiculos.entities.Country;
import senac.dws.veiculos.exceptions.BrandException;
import senac.dws.veiculos.exceptions.CountryException;
import senac.dws.veiculos.exceptions.InvalidRequestException;
import senac.dws.veiculos.repositories.BrandRepository;
import senac.dws.veiculos.repositories.CountryRepository;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;

    public BrandService(BrandRepository brandRepository, CountryRepository countryRepository) {
        this.brandRepository = brandRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Brand> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandException("Marca não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Brand> searchByName(String name) {
        return brandRepository.findByNameContainingIgnoreCase(name);
    }

    public Brand create(Brand brand) {
        brand.setCountry(resolveCountry(brand.getCountry()));
        return brandRepository.save(brand);
    }

    public Brand update(Long id, Brand input) {
        Brand brand = findById(id);
        brand.setName(input.getName());
        brand.setCountry(resolveCountry(input.getCountry()));
        return brandRepository.save(brand);
    }

    public void deleteById(Long id) {
        Brand brand = findById(id);
        brandRepository.delete(brand);
    }

    private Country resolveCountry(Country ref) {
        if (ref == null || ref.getId() == null) {
            throw new InvalidRequestException("País (id) é obrigatório");
        }
        return countryRepository.findById(ref.getId())
                .orElseThrow(() -> new CountryException("País não encontrado"));
    }
}
