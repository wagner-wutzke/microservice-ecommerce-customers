package net.wowdev.ecommerce.cutomers.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.PaymentMethodDTO;
import net.wowdev.ecommerce.domain.events.CustomerDataFailedEvent;
import net.wowdev.ecommerce.domain.events.CustomerDataLoadedEvent;
import net.wowdev.ecommerce.domain.events.PaymentMethodLoadedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class CustomerProducerTest {
  private final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
  private final CustomerProducer producer = new CustomerProducer(template, "customer-events");

  @Test
  void publishesLoadedCustomerUsingEventId() {
    UUID eventId = UUID.randomUUID();
    CustomerDataLoadedEvent event =
        new CustomerDataLoadedEvent(eventId, "transaction-1", new CustomerDTO(), Instant.now());

    producer.publish(event);

    verify(template).send("customer-events", eventId.toString(), event);
  }

  @Test
  void publishesFailedCustomerUsingEventId() {
    UUID eventId = UUID.randomUUID();
    CustomerDataFailedEvent event =
        new CustomerDataFailedEvent(eventId, "transaction-1", null, "not found", Instant.now());

    producer.publish(event);

    verify(template).send("customer-events", eventId.toString(), event);
  }

  @Test
  void publishesPaymentMethodUsingEventId() {
    UUID eventId = UUID.randomUUID();
    PaymentMethodLoadedEvent event =
        new PaymentMethodLoadedEvent(
            eventId, "transaction-1", new PaymentMethodDTO(), Instant.now());

    producer.publish(event);

    verify(template).send("customer-events", eventId.toString(), event);
  }
}
