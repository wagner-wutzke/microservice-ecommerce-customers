package net.wowdev.ecommerce.cutomers.controller;

import net.wowdev.ecommerce.cutomers.service.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {
    @Test
    void mapsNotFound() {
        final var detail = new ApiExceptionHandler().notFound(new CustomerNotFoundException(UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
    }
}
