package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.CountryController;
import senac.dws.veiculos.entities.Country;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler implements RepresentationModelAssembler<Country, EntityModel<Country>> {

    @Override
    public EntityModel<Country> toModel(Country entity) {
        EntityModel<Country> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(CountryController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(CountryController.class).withRel("collection"));
        return model;
    }
}
