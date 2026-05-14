package senac.dws.veiculos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import senac.dws.veiculos.idempotency.IdempotencyKeyInterceptor;

/**
 * Registo de interceptores Spring MVC. O rate limiting é um {@link jakarta.servlet.Filter} registado
 * em {@link RateLimitConfiguration} (corre antes dos interceptores). Idempotência de POST vive aqui.
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
