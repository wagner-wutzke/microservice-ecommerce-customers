package net.wowdev.ecommerce.cutomers.config;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, CustomerDTO> customerProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") final String brokers,
            @Value("${spring.kafka.producer.retries:5}") final int retries) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, CustomerDTO> customerKafkaTemplate(
            final ProducerFactory<String, CustomerDTO> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public ConsumerFactory<String, CustomerDTO> customerConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") final String brokers,
            @Value("${spring.kafka.consumer.group-id}") final String groupId) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        properties.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "net.wowdev.ecommerce.domain.dto");
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(),
                new JacksonJsonDeserializer<>(CustomerDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> customerKafkaListenerContainerFactory(
            final ConsumerFactory<String, CustomerDTO> consumerFactory,
            final KafkaTemplate<String, CustomerDTO> template,
            @Value("${spring.kafka.consumer.max-attempts:5}") final long attempts) {
        final ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(1000L, attempts - 1)));
        return factory;
    }
}
