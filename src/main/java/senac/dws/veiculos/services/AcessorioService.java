package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Acessorio;
import senac.dws.veiculos.exceptions.AcessorioException;
import senac.dws.veiculos.repositories.AcessorioRepository;

import java.util.List;

@Service
public class AcessorioService {

    private final AcessorioRepository acessorioRepository;

    public AcessorioService(AcessorioRepository acessorioRepository) {
        this.acessorioRepository = acessorioRepository;
    }

    @Transactional(readOnly = true)
    public Page<Acessorio> findAll(Pageable pageable) {
        return acessorioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Acessorio findById(Long id) {
        return acessorioRepository.findById(id)
                .orElseThrow(() -> new AcessorioException("Acessório não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Acessorio> searchByNome(String nome) {
        return acessorioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Acessorio create(Acessorio acessorio) {
        return acessorioRepository.save(acessorio);
    }

    public Acessorio update(Long id, Acessorio input) {
        Acessorio acessorio = findById(id);
        acessorio.setNome(input.getNome());
        acessorio.setDescricao(input.getDescricao());
        return acessorioRepository.save(acessorio);
    }

    public void deleteById(Long id) {
        Acessorio acessorio = findById(id);
        acessorioRepository.delete(acessorio);
    }
}
