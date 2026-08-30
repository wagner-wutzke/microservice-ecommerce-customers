package net.wowdev.ecommerce.cutomers.controller;

import net.wowdev.ecommerce.cutomers.service.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {
    @Test
    void mapsNotFound() {
        final var detail = new ApiExceptionHandler().notFound(new CustomerNotFoundException(UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
    }

    @Test
    void mapsValidationErrors() {
        final BindingResult bindingResult = mock(BindingResult.class);
        final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                java.util.List.of(new FieldError("customer", "email", "must be valid")));

        final var detail = new ApiExceptionHandler().invalid(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), detail.getStatus());
        assertEquals("email: must be valid", detail.getDetail());
    }
}
