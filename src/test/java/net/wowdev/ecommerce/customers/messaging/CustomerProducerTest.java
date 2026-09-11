package net.wowdev.ecommerce.customers.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.customers.service.MessagingCustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.dto.PaymentMethodDTO;
import net.wowdev.ecommerce.domain.events.CustomerReplicationCompleted;
import net.wowdev.ecommerce.domain.events.CustomerReplicationFailed;
import net.wowdev.ecommerce.domain.events.PaymentMethodReplicationCompleted;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class CustomerProducerTest {
  private final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
  private final CustomerProducer producer = new CustomerProducer(template, "customer-events");

  @Test
  void publishesLoadedCustomerUsingTransactionId() {
    UUID eventId = UUID.randomUUID();
    CustomerReplicationCompleted event =
        new CustomerReplicationCompleted(
            eventId,
            "transaction-1",
            new CustomerDTO(),
            Instant.now(),
            MessagingCustomerService.ORIGIN_SERVICE);

    producer.publish(event);

    verify(template).send("customer-events", "transaction-1", event);
  }

  @Test
  void publishesFailedCustomerUsingTransactionId() {
    UUID eventId = UUID.randomUUID();
    CustomerReplicationFailed event =
        new CustomerReplicationFailed(
            eventId,
            "transaction-1",
            null,
            "customer could not be loaded",
            Instant.now(),
            MessagingCustomerService.ORIGIN_SERVICE);

    producer.publish(event);

    verify(template).send("customer-events", "transaction-1", event);
  }

  @Test
  void publishesPaymentMethodUsingTransactionId() {
    UUID eventId = UUID.randomUUID();
    PaymentMethodReplicationCompleted event =
        new PaymentMethodReplicationCompleted(
            eventId,
            "transaction-1",
            new PaymentMethodDTO(),
            Instant.now(),
            MessagingCustomerService.ORIGIN_SERVICE);

    producer.publish(event);

    verify(template).send("customer-events", "transaction-1", event);
  }
}
