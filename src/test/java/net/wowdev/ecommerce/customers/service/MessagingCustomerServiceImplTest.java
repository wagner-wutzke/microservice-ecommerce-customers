package net.wowdev.ecommerce.customers.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.wowdev.ecommerce.customers.messaging.CustomerProducer;
import net.wowdev.ecommerce.customers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.dto.PaymentMethodDTO;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import net.wowdev.ecommerce.domain.entity.PaymentMethodEntity;
import net.wowdev.ecommerce.domain.enums.CustomerStatus;
import net.wowdev.ecommerce.domain.events.CustomerReplicationCompleted;
import net.wowdev.ecommerce.domain.events.CustomerReplicationFailed;
import net.wowdev.ecommerce.domain.events.OrderCreated;
import net.wowdev.ecommerce.domain.events.PaymentMethodReplicationCompleted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MessagingCustomerServiceImplTest {

  @Mock private CustomerRepository repository;

  @Mock private CustomerProducer producer;

  private MessagingCustomerService service;

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

  private static OrderCreated orderEvent(UUID customerId) {
    OrderDTO order = new OrderDTO();
    order.setCustomerId(customerId);
    return new OrderCreated(
        UUID.randomUUID(),
        "transaction-1",
        order,
        Instant.now(),
        MessagingCustomerService.ORIGIN_SERVICE);
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new MessagingCustomerServiceImpl(producer, repository);
    id = UUID.randomUUID();
  }

  @Test
  void publishesPaymentMethodThenCustomerWithoutPaymentDetails() {
    PaymentMethodDTO payment = new PaymentMethodDTO();
    CustomerDTO customer = dto(id, "Ada");
    customer.setPaymentMethods(List.of(payment));
    OrderCreated event = orderEvent(id);
    when(repository.findById(id)).thenReturn(Optional.of(entityWithPayment(id)));

    service.process(event);

    ArgumentCaptor<PaymentMethodReplicationCompleted> paymentEvent =
        ArgumentCaptor.forClass(PaymentMethodReplicationCompleted.class);
    ArgumentCaptor<CustomerReplicationCompleted> customerEvent =
        ArgumentCaptor.forClass(CustomerReplicationCompleted.class);
    verify(producer).publish(paymentEvent.capture());
    verify(producer).publish(customerEvent.capture());
    assertThat(paymentEvent.getValue().paymentMethodDTO()).isNotNull();
    CustomerReplicationCompleted loaded = customerEvent.getValue();
    assertThat(loaded.customerDTO().getPaymentMethods()).isEmpty();
  }

  @Test
  void publishesFailureWhenCustomerCannotBeLoaded() {
    when(repository.findById(id)).thenReturn(Optional.empty());
    OrderCreated event = orderEvent(id);

    service.process(event);

    ArgumentCaptor<CustomerReplicationFailed> failure =
        ArgumentCaptor.forClass(CustomerReplicationFailed.class);
    verify(producer).publish(failure.capture());
    assertThat(failure.getValue().transactionId()).isEqualTo("transaction-1");
    assertThat(failure.getValue().reason()).isEqualTo("Customer not found: " + id);
  }

  @Test
  void publishesFailureWhenCustomerHasNoPaymentMethods() {
    when(repository.findById(id)).thenReturn(Optional.of(entity(id, "Ada")));

    service.process(orderEvent(id));

    verify(producer).publish(any(CustomerReplicationFailed.class));
  }
}
