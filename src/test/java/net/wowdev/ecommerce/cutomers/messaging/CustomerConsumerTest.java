package net.wowdev.ecommerce.cutomers.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.junit.jupiter.api.Test;

class CustomerConsumerTest {
  @Test
  void delegatesOrderProcessingEvent() {
    CustomerService service = mock(CustomerService.class);
    CustomerConsumer consumer = new CustomerConsumer(service);
    OrderProcessingStartedEvent event =
        new OrderProcessingStartedEvent(
            UUID.randomUUID(), "transaction-1", new OrderDTO(), Instant.now());

    consumer.handleOrderProcessingStarted(event);

    verify(service).handleOrderProcessingStarted(event);
  }
}
