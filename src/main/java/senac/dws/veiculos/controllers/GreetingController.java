package senac.dws.veiculos.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Demo", description = "Endpoint demonstrativo de saudação com links HATEOAS.")
@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Operation(summary = "Retorna uma saudação com contador")
    @ApiResponse(responseCode = "200", description = "Saudação gerada")
    @GetMapping("/api/v1/greetings")
    public ResponseEntity<EntityModel<Greeting>> greeting(
            @RequestParam(defaultValue = "Oláaaaaa") String text) {
        Greeting body = new Greeting(counter.incrementAndGet(), template.formatted(text));
        EntityModel<Greeting> model = EntityModel.of(body);
        model.add(linkTo(methodOn(GreetingController.class).greeting(text)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @Schema(description = "Resposta de saudação com contador de requisições")
    public static class Greeting {
        @Schema(description = "Contador incremental", example = "42")
        public long id;
        @Schema(description = "Mensagem formatada", example = "Hello, Mundo!")
        public String text;

        public Greeting(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }
}
