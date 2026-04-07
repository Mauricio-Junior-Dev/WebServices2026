package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.FuelTypeController;
import senac.dws.veiculos.entities.FuelType;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FuelTypeModelAssembler implements RepresentationModelAssembler<FuelType, EntityModel<FuelType>> {

    @Override
    public EntityModel<FuelType> toModel(FuelType entity) {
        EntityModel<FuelType> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(FuelTypeController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(FuelTypeController.class).withRel("collection"));
        return model;
    }
}
