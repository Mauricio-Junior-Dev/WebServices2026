package senac.dws.veiculos.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import senac.dws.veiculos.controllers.CategoryController;
import senac.dws.veiculos.entities.Category;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<Category>> {

    @Override
    public EntityModel<Category> toModel(Category entity) {
        EntityModel<Category> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(CategoryController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(CategoryController.class).withRel("collection"));
        return model;
    }
}
