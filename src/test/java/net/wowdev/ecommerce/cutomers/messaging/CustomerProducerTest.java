package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.events.CustomerDataLoadedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CustomerProducerTest {
    @Test
    void publishesUsingCustomerIdAsKey() {
        final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
        final CustomerProducer producer = new CustomerProducer(template, "customer-events-topic");
        final CustomerDTO customer = new CustomerDTO();
        UUID customerId = UUID.randomUUID();
        customer.setId(customerId);

        CustomerDataLoadedEvent event = new CustomerDataLoadedEvent(
                customerId,
                "tx_id",
                customer,
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
        producer.publish(event);
        verify(template).send("customer-events-topic", customer.getId().toString(), event);
    }
}
