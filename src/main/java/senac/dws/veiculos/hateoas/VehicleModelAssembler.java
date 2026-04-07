package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.*;
import senac.dws.veiculos.entities.Vehicle;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VehicleModelAssembler implements RepresentationModelAssembler<Vehicle, EntityModel<Vehicle>> {

    @Override
    public EntityModel<Vehicle> toModel(Vehicle entity) {
        EntityModel<Vehicle> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(VehicleController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(VehicleController.class).withRel("collection"));

        if (entity.getBrand() != null) {
            model.add(linkTo(methodOn(BrandController.class).getById(entity.getBrand().getId())).withRel("brand"));
        }
        if (entity.getCategory() != null) {
            model.add(linkTo(methodOn(CategoryController.class).getById(entity.getCategory().getId())).withRel("category"));
        }
        if (entity.getEngine() != null) {
            model.add(linkTo(methodOn(EngineController.class).getById(entity.getEngine().getId())).withRel("engine"));
        }
        if (entity.getDocumentacao() != null) {
            model.add(linkTo(methodOn(DocumentacaoController.class).getById(entity.getDocumentacao().getId())).withRel("documentacao"));
        }
        return model;
    }
}
