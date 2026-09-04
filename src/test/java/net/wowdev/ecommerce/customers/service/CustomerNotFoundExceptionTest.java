package net.wowdev.ecommerce.customers.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerNotFoundExceptionTest {
  @Test
  void describesMissingCustomer() {
    UUID id = UUID.randomUUID();

    assertThat(new CustomerNotFoundException(id).getMessage())
        .isEqualTo("Customer not found: " + id);
  }
}
