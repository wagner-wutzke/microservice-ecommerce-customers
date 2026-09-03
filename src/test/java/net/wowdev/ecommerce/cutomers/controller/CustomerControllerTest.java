package net.wowdev.ecommerce.cutomers.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.wowdev.ecommerce.cutomers.service.CrudCustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

class CustomerControllerTest {
  @Mock private CrudCustomerService service;
  private CustomerController controller;
  private UUID id;
  private CustomerDTO customer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    controller = new CustomerController(service);
    id = UUID.randomUUID();
    customer = new CustomerDTO();
    customer.setId(id);
  }

  @Test
  void delegatesFindOperations() {
    when(service.findById(id)).thenReturn(customer);
    when(service.findAll(2, 5)).thenReturn(new PageImpl<>(List.of(customer)));

    assertThat(controller.findById(id)).isSameAs(customer);
    assertThat(controller.findAll(2, 5).getContent()).containsExactly(customer);
    verify(service).findById(id);
    verify(service).findAll(2, 5);
  }

  @Test
  void createsResourceWithLocationHeader() {
    when(service.create(customer)).thenReturn(customer);

    var response = controller.create(customer);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getHeaders().getLocation()).hasToString("/api/v1/customers/" + id);
    assertThat(response.getBody()).isSameAs(customer);
  }

  @Test
  void delegatesUpdateAndDelete() {
    when(service.update(id, customer)).thenReturn(customer);

    assertThat(controller.update(id, customer)).isSameAs(customer);
    var response = controller.delete(id);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(service).update(id, customer);
    verify(service).delete(id);
  }
}
