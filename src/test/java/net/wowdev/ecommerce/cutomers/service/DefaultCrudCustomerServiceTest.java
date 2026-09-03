package net.wowdev.ecommerce.cutomers.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.wowdev.ecommerce.cutomers.messaging.CustomerProducer;
import net.wowdev.ecommerce.cutomers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import net.wowdev.ecommerce.domain.entity.PaymentMethodEntity;
import net.wowdev.ecommerce.domain.enums.CustomerStatus;
import net.wowdev.ecommerce.domain.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class DefaultCrudCustomerServiceTest {
  @Mock private CustomerRepository repository;
  @Mock private CustomerProducer producer;
  private DefaultCrudCustomerService service;
  private UUID id;

  private static CustomerDTO dto(UUID customerId, String firstName) {
    return new CustomerDTO(
        customerId,
        firstName,
        "User",
        "new@example.com",
        CustomerStatus.INACTIVE,
        List.of(),
        "New street",
        "Suite 2",
        "New city",
        "SP",
        "22222",
        "BR",
        null,
        null);
  }

  private static CustomerEntity entity(UUID customerId, String firstName) {
    return new CustomerEntity(
        customerId,
        firstName,
        "User",
        "ada@example.com",
        CustomerStatus.ACTIVE,
        List.of(),
        "Old street",
        "Old suite",
        "Old city",
        "RJ",
        "11111",
        "BR",
        Instant.now(),
        Instant.now());
  }

  private static CustomerEntity entityWithPayment(UUID customerId) {
    PaymentMethodEntity payment =
        new PaymentMethodEntity(
            UUID.randomUUID(),
            customerId,
            "4111",
            "Ada",
            "12/30",
            123,
            "Visa",
            Instant.now(),
            Instant.now());
    return new CustomerEntity(
        customerId,
        "Ada",
        "User",
        "ada@example.com",
        CustomerStatus.ACTIVE,
        List.of(payment),
        "Street",
        null,
        "City",
        "SP",
        "11111",
        "BR",
        Instant.now(),
        Instant.now());
  }

  private static OrderCreatedEvent orderEvent(UUID customerId) {
    OrderDTO order = new OrderDTO();
    order.setCustomerId(customerId);
    return new OrderCreatedEvent(
        UUID.randomUUID(),
        "transaction-1",
        order,
        Instant.now(),
        MessagingCustomerService.ORIGIN_SERVICE);
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new DefaultCrudCustomerService(repository);
    id = UUID.randomUUID();
  }

  @Test
  void findsCustomerById() {
    CustomerEntity entity = entity(id, "Ada");
    when(repository.findById(id)).thenReturn(Optional.of(entity));

    CustomerDTO result = service.findById(id);

    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getFirstName()).isEqualTo("Ada");
  }

  @Test
  void rejectsMissingCustomerById() {
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(id))
        .isInstanceOf(CustomerNotFoundException.class)
        .hasMessage("Customer not found: " + id);
  }

  @Test
  void findsAllCustomersWithRequestedPage() {
    CustomerEntity entity = entity(id, "Ada");
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(entity)));

    Page<CustomerDTO> result = service.findAll(1, 10);

    assertThat(result.getContent())
        .singleElement()
        .extracting(CustomerDTO::getFirstName)
        .isEqualTo("Ada");
    ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(pageable.capture());
    assertThat(pageable.getValue().getPageNumber()).isEqualTo(1);
    assertThat(pageable.getValue().getPageSize()).isEqualTo(10);
    assertThat(pageable.getValue().getSort().getOrderFor("createdAt").isDescending()).isTrue();
  }

  @Test
  void createsCustomerAndSetsIdentityAndAuditDates() {
    CustomerDTO input = dto(null, "Grace");
    when(repository.save(any(CustomerEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerDTO result = service.create(input);

    assertThat(result.getId()).isNotNull();
    assertThat(result.getFirstName()).isEqualTo("Grace");
    assertThat(result.getModifiedAt()).isEqualTo(result.getCreatedAt());
    verify(repository).save(any(CustomerEntity.class));
  }

  @Test
  void updatesEveryMutableCustomerField() {
    CustomerEntity current = entity(id, "Old");
    CustomerDTO replacement = dto(UUID.randomUUID(), "New");
    when(repository.findById(id)).thenReturn(Optional.of(current));
    when(repository.save(current)).thenReturn(current);

    CustomerDTO result = service.update(id, replacement);

    assertThat(result.getId()).isEqualTo(id);
    assertThat(current.getFirstName()).isEqualTo("New");
    assertThat(current.getLastName()).isEqualTo("User");
    assertThat(current.getEmail()).isEqualTo("new@example.com");
    assertThat(current.getCustomerStatus()).isEqualTo(CustomerStatus.INACTIVE);
    assertThat(current.getAddressLine1()).isEqualTo("New street");
    assertThat(current.getAddressLine2()).isEqualTo("Suite 2");
    assertThat(current.getCity()).isEqualTo("New city");
    assertThat(current.getCountry()).isEqualTo("BR");
    assertThat(current.getPostalCode()).isEqualTo("22222");
    assertThat(current.getStateProvince()).isEqualTo("SP");
    assertThat(current.getModifiedAt()).isNotNull();
    verify(repository).save(current);
  }

  @Test
  void updateRejectsMissingCustomer() {
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(id, dto(null, "New")))
        .isInstanceOf(CustomerNotFoundException.class);
    verify(repository, never()).save(any(CustomerEntity.class));
  }

  @Test
  void deletesExistingCustomer() {
    when(repository.existsById(id)).thenReturn(true);

    service.delete(id);

    verify(repository).deleteById(id);
  }

  @Test
  void deleteRejectsMissingCustomer() {
    when(repository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(id)).isInstanceOf(CustomerNotFoundException.class);
    verify(repository, never()).deleteById(id);
  }
}
