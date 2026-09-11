package net.wowdev.ecommerce.customers.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.customers.service.MessagingCustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.events.CustomerReplicationRequested;
import org.junit.jupiter.api.Test;

class CustomerConsumerTest {

  private final MessagingCustomerService service =
      mock(MessagingCustomerService.class);
  private final CustomerConsumer consumer = new CustomerConsumer(service);

  @Test
  void delegatesOrderCreatedEvent() {
    MessagingCustomerService service = mock(MessagingCustomerService.class);
    CustomerConsumer consumer = new CustomerConsumer(service);
    CustomerReplicationRequested event =
        new CustomerReplicationRequested(
            UUID.randomUUID(),
            "transaction-1",
            new CustomerDTO(),
            Instant.now(),
            MessagingCustomerService.ORIGIN_SERVICE);

    consumer.handle(event);

    verify(service).process(event);
  }

  @Test
  void handlesUnknownEvents() {
    consumer.handleUnknown(new Object());
  }
}
