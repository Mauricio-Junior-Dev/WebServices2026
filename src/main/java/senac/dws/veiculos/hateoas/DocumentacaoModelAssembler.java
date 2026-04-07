package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.DocumentacaoController;
import senac.dws.veiculos.controllers.VehicleController;
import senac.dws.veiculos.entities.Documentacao;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DocumentacaoModelAssembler implements RepresentationModelAssembler<Documentacao, EntityModel<Documentacao>> {

    @Override
    public EntityModel<Documentacao> toModel(Documentacao entity) {
        EntityModel<Documentacao> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(DocumentacaoController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(DocumentacaoController.class).withRel("collection"));
        if (entity.getVehicle() != null) {
            model.add(linkTo(methodOn(VehicleController.class).getById(entity.getVehicle().getId())).withRel("vehicle"));
        }
        return model;
    }
}
