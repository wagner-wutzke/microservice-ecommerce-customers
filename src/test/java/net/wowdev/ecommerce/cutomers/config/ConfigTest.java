package net.wowdev.ecommerce.cutomers.config;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigTest {
    @Test
    void createsJacksonAndPersistenceConfiguration() {
        final JacksonConfig jackson = new JacksonConfig();
        assertNotNull(jackson.javaTimeModule());
        assertNotNull(jackson.timestampSerializationFeature());
        assertNotNull(new PersistenceConfig());
    }

    @Test
    void createsKafkaFactories() {
        final KafkaConfig config = new KafkaConfig();
        final ProducerFactory<String, CustomerDTO> producer = config.customerProducerFactory("localhost:9092", 5);
        final KafkaTemplate<String, CustomerDTO> template = config.customerKafkaTemplate(producer);
        final ConsumerFactory<String, CustomerDTO> consumer = config.customerConsumerFactory("localhost:9092", "test");
        assertNotNull(template);
        assertNotNull(consumer);
        assertNotNull(config.customerKafkaListenerContainerFactory(consumer, template, 5));
    }
}
