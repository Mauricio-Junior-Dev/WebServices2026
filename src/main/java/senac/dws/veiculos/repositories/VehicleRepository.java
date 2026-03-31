package senac.dws.veiculos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.dws.veiculos.entities.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    //Busca o Veiculo informado ignorando maiusculas e minusculas sem a necessidade de escrever sql
    List<Vehicle> findByNameContainingIgnoreCase(String name);

    //Busca de veiculo por ano especifico
    List<Vehicle> findByYear(Integer year);


    //Busca veiculo por ID da marca (Brand)
    List<Vehicle> findByBrandId(Long brandId);


    //Busca veiculo com preço menor que o valor informado
    List<Vehicle> findByPriceLessThan(Double price);

}
