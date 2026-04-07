package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.EngineController;
import senac.dws.veiculos.controllers.FuelTypeController;
import senac.dws.veiculos.entities.Engine;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EngineModelAssembler implements RepresentationModelAssembler<Engine, EntityModel<Engine>> {

    @Override
    public EntityModel<Engine> toModel(Engine entity) {
        EntityModel<Engine> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(EngineController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(EngineController.class).withRel("collection"));
        if (entity.getFuelType() != null) {
            model.add(linkTo(methodOn(FuelTypeController.class).getById(entity.getFuelType().getId())).withRel("fuelType"));
        }
        return model;
    }
}
