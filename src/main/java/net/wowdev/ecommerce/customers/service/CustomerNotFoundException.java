package net.wowdev.ecommerce.customers.service;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {
  public CustomerNotFoundException(final UUID id) {
    super("Customer not found: " + id);
  }
}
