package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Documentacao;
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.exceptions.ConflictException;
import senac.dws.veiculos.exceptions.DocumentacaoException;
import senac.dws.veiculos.exceptions.InvalidRequestException;
import senac.dws.veiculos.exceptions.VehicleException;
import senac.dws.veiculos.repositories.DocumentacaoRepository;
import senac.dws.veiculos.repositories.VehicleRepository;

@Service
public class DocumentacaoService {

    private final DocumentacaoRepository documentacaoRepository;
    private final VehicleRepository vehicleRepository;

    public DocumentacaoService(DocumentacaoRepository documentacaoRepository,
                               VehicleRepository vehicleRepository) {
        this.documentacaoRepository = documentacaoRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public Page<Documentacao> findAll(Pageable pageable) {
        return documentacaoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Documentacao findById(Long id) {
        return documentacaoRepository.findDetailById(id)
                .orElseThrow(() -> new DocumentacaoException("Documentação não encontrada"));
    }

    public Documentacao create(Documentacao input) {
        if (documentacaoRepository.existsByNumeroRegistro(input.getNumeroRegistro())) {
            throw new ConflictException("Número de registro já cadastrado");
        }
        Vehicle vehicle = vehicleRepository.findById(requireVehicleId(input))
                .orElseThrow(() -> new VehicleException("Veículo não encontrado"));
        if (vehicle.getDocumentacao() != null) {
            throw new ConflictException("Veículo já possui documentação");
        }
        Documentacao doc = new Documentacao();
        doc.setNumeroRegistro(input.getNumeroRegistro());
        doc.setObservacao(input.getObservacao());
        vehicle.setDocumentacao(doc);
        vehicleRepository.save(vehicle);
        return vehicle.getDocumentacao();
    }

    public Documentacao update(Long id, Documentacao input) {
        Documentacao doc = findById(id);
        if (documentacaoRepository.existsByNumeroRegistroAndIdNot(input.getNumeroRegistro(), id)) {
            throw new ConflictException("Número de registro já cadastrado");
        }
        doc.setNumeroRegistro(input.getNumeroRegistro());
        doc.setObservacao(input.getObservacao());
        return documentacaoRepository.save(doc);
    }

    public void deleteById(Long id) {
        Documentacao doc = findById(id);
        Vehicle vehicle = doc.getVehicle();
        if (vehicle != null) {
            vehicle.setDocumentacao(null);
            vehicleRepository.save(vehicle);
        } else {
            documentacaoRepository.delete(doc);
        }
    }

    private Long requireVehicleId(Documentacao input) {
        if (input.getVehicle() == null || input.getVehicle().getId() == null) {
            throw new InvalidRequestException("Veículo (id) é obrigatório");
        }
        return input.getVehicle().getId();
    }
}
