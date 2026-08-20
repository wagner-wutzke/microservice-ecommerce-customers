package net.wowdev.ecommerce.cutomers.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public SerializationFeature timestampSerializationFeature() {
        return SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }
}
