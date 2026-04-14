package com.ziminpro.twitter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configures the application-wide {@link ObjectMapper}.
 *
 * <p>Without this, Spring WebFlux's default mapper serializes
 * {@link java.time.LocalDateTime} as a numeric array
 * ({@code [2024,11,12,14,38,29]}).  Registering {@link JavaTimeModule}
 * and disabling {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS}
 * produces ISO-8601 strings ({@code "2024-11-12T14:38:29"}) instead.</p>
 *
 * <p>The {@code @Primary} annotation ensures this bean takes precedence
 * over any auto-configured mapper Spring Boot may provide.</p>
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}