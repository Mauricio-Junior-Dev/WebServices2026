package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.AcessorioController;
import senac.dws.veiculos.entities.Acessorio;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AcessorioModelAssembler implements RepresentationModelAssembler<Acessorio, EntityModel<Acessorio>> {

    @Override
    public EntityModel<Acessorio> toModel(Acessorio entity) {
        EntityModel<Acessorio> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(AcessorioController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(AcessorioController.class).withRel("collection"));
        return model;
    }
}
