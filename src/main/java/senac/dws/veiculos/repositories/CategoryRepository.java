package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
