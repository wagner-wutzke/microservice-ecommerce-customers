package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomerConsumerTest {
    @Test
    void consumesEvent() {
        assertDoesNotThrow(() -> {
            new CustomerConsumer(null, null)
                    .handleOrderCreated(new OrderProcessingStartedEvent(
                            UUID.randomUUID(),
                            "TX_ID",
                            new OrderDTO(),
                            Instant.now()
                    ));
        });
    }
}
