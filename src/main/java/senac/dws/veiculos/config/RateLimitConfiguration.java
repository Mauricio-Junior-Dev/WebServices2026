package senac.dws.veiculos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import senac.dws.veiculos.ratelimit.RateLimitBucketManager;
import senac.dws.veiculos.ratelimit.RateLimitProperties;
import senac.dws.veiculos.ratelimit.RateLimitingFilter;

/**
 * Regista o filtro de rate limiting com ordem alta para correr <strong>antes</strong> da lógica
 * dispendiosa (controllers, idempotência com base de dados, etc.).
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfiguration {

    @Bean
    public RateLimitingFilter rateLimitingFilter(
            RateLimitProperties properties,
            RateLimitBucketManager bucketManager) {
        return new RateLimitingFilter(properties, bucketManager);
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(RateLimitingFilter filter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}
