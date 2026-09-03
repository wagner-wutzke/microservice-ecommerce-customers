package net.wowdev.ecommerce.cutomers.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import net.wowdev.ecommerce.cutomers.service.MessagingCustomerService;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

class CustomerConsumerTest {
  @Test
  void delegatesOrderCreatedEvent() {
    MessagingCustomerService service = mock(MessagingCustomerService.class);
    CustomerConsumer consumer = new CustomerConsumer(service);
    OrderCreatedEvent event =
        new OrderCreatedEvent(
                UUID.randomUUID(),
                "transaction-1",
                new OrderDTO(),
                Instant.now(),
                MessagingCustomerService.ORIGIN_SERVICE);

    consumer.handle(event);

    verify(service).handleOrderCreated(event);
  }
}
