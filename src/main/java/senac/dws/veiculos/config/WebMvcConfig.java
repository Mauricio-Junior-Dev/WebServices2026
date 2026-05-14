package senac.dws.veiculos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import senac.dws.veiculos.idempotency.IdempotencyKeyInterceptor;

/**
 * Registo de interceptores transversais à API. Idempotência de POST vive aqui;
 * limitação de taxa (por IP, 429, cabeçalhos de quota) deve ser um interceptor/filtro separado.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final IdempotencyKeyInterceptor idempotencyKeyInterceptor;

    public WebMvcConfig(IdempotencyKeyInterceptor idempotencyKeyInterceptor) {
        this.idempotencyKeyInterceptor = idempotencyKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempotencyKeyInterceptor).addPathPatterns("/api/**");
    }
}
