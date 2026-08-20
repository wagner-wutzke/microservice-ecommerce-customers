package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomerConsumerTest {
    @Test
    void consumesEvent() {
        assertDoesNotThrow(() -> new CustomerConsumer().consume(new CustomerDTO()));
    }
}
