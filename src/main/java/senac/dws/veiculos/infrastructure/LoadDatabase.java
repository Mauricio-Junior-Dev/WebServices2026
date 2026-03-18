package senac.dws.veiculos.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import senac.dws.veiculos.entities.*;
import senac.dws.veiculos.repositories.*;

@Configuration
public class LoadDatabase { //Popula o banco de dados com um veiculo de teste, para verificar se a conexão com o banco esta funcionando
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            VehicleRepository vehicleRepository,
            CategoryRepository categoryRepository,
            FuelTypeRepository fuelTypeRepository,
            EngineRepository engineRepository,
            BrandRepository brandRepository
            ) {
        return args -> {
            Brand toyota = new Brand();
            toyota.setName("Toyota");
            brandRepository.save(toyota);

            Category sedan = new Category();
            sedan.setName("Sedan");
            categoryRepository.save(sedan);

            FuelType flex = new FuelType();
            flex.setName("Flex");
            fuelTypeRepository.save(flex);


            Engine engine1 = new Engine();
            engine1.setType("2.0 Aspirado");
            engine1.setDisplacement(2.0);
            engine1.setHorsepower(150);
            engine1.setFuelType(flex);
            engineRepository.save(engine1);

            Vehicle v1 = new Vehicle();
            v1.setName("Corolla");
            v1.setYear(2022);
            v1.setPrice(150000.0);
            v1.setBrand(toyota);
            v1.setCategory(sedan);
            v1.setEngine(engine1);
            vehicleRepository.save(v1);

        };
    }

}
