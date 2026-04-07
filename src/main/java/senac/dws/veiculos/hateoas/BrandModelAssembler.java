package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.BrandController;
import senac.dws.veiculos.controllers.CountryController;
import senac.dws.veiculos.entities.Brand;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BrandModelAssembler implements RepresentationModelAssembler<Brand, EntityModel<Brand>> {

    @Override
    public EntityModel<Brand> toModel(Brand entity) {
        EntityModel<Brand> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(BrandController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(BrandController.class).withRel("collection"));
        if (entity.getCountry() != null) {
            model.add(linkTo(methodOn(CountryController.class).getById(entity.getCountry().getId())).withRel("country"));
        }
        return model;
    }
}
