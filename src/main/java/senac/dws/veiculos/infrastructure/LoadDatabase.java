package senac.dws.veiculos.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import senac.dws.veiculos.entities.Vehicle;
import senac.dws.veiculos.repositories.VehicleRepository;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(VehicleRepository repository) {
        return args ->  {
            Vehicle v = new Vehicle();
            v.setName("Isso teste");
            v.setDescription("Testando metodo novo");
            repository.save(v);
            log.info("Preloading" + repository.findAll());
        };
    }

}
