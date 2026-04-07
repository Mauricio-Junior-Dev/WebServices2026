package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Country;
import senac.dws.veiculos.exceptions.CountryException;
import senac.dws.veiculos.exceptions.EntidadeJaCadastradaException;
import senac.dws.veiculos.repositories.CountryRepository;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Country> findAll(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Country findById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new CountryException("País não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Country> searchByName(String name) {
        return countryRepository.findByNameContainingIgnoreCase(name);
    }

    public Country create(Country country) {
        if (countryRepository.existsByNameIgnoreCase(country.getName())) {
            throw new EntidadeJaCadastradaException("Já existe um país cadastrado com este nome.");
        }
        return countryRepository.save(country);
    }

    public Country update(Long id, Country input) {
        Country country = findById(id);
        if (countryRepository.existsByNameIgnoreCaseAndIdNot(input.getName(), id)) {
            throw new EntidadeJaCadastradaException("Já existe um país cadastrado com este nome.");
        }
        country.setName(input.getName());
        return countryRepository.save(country);
    }

    public void deleteById(Long id) {
        Country country = findById(id);
        countryRepository.delete(country);
    }
}
