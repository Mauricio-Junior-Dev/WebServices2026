package senac.dws.veiculos.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.dws.veiculos.entities.Category;
import senac.dws.veiculos.exceptions.CategoryException;
import senac.dws.veiculos.exceptions.EntidadeJaCadastradaException;
import senac.dws.veiculos.repositories.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Categoria não encontrada"));
    }

    @Transactional(readOnly = true)
    public Page<Category> searchByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Category create(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new EntidadeJaCadastradaException("Já existe uma categoria cadastrada com este nome.");
        }
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category input) {
        Category category = findById(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(input.getName(), id)) {
            throw new EntidadeJaCadastradaException("Já existe uma categoria cadastrada com este nome.");
        }
        category.setName(input.getName());
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
