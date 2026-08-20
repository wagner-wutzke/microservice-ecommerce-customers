package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CustomerProducerTest {
    @Test
    void publishesUsingCustomerIdAsKey() {
        final KafkaTemplate<String, CustomerDTO> template = mock(KafkaTemplate.class);
        final CustomerProducer producer = new CustomerProducer(template, "customer-change-topic");
        final CustomerDTO customer = new CustomerDTO();
        customer.setId(UUID.randomUUID());
        producer.publish(customer);
        verify(template).send("customer-change-topic", customer.getId().toString(), customer);
    }
}
