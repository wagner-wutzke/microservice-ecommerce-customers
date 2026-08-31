package net.wowdev.ecommerce.cutomers.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.wowdev.ecommerce.cutomers.service.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerTest {
  @Test
  void createsNotFoundProblemDetail() {
    CustomerNotFoundException exception = new CustomerNotFoundException(UUID.randomUUID());
    var detail = new ApiExceptionHandler().notFound(exception);

    assertThat(detail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(detail.getDetail()).isEqualTo(exception.getMessage());
  }

  @Test
  void joinsAllValidationErrors() {
    BindingResult bindingResult = mock(BindingResult.class);
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors())
        .thenReturn(
            List.of(
                new FieldError("customer", "email", "must be valid"),
                new FieldError("customer", "firstName", "must not be blank")));

    var detail = new ApiExceptionHandler().invalid(exception);

    assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(detail.getDetail()).isEqualTo("email: must be valid, firstName: must not be blank");
  }
}
