package net.wowdev.ecommerce.customers.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConfigTest {
  private static KafkaConfig configured() {
    KafkaConfig config = new KafkaConfig();
    ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
    ReflectionTestUtils.setField(config, "consumerGroup", "customers");
    ReflectionTestUtils.setField(config, "acks", "all");
    ReflectionTestUtils.setField(config, "deliveryTimeout", "30000");
    ReflectionTestUtils.setField(config, "linger", "0");
    ReflectionTestUtils.setField(config, "requestTimeout", "10000");
    ReflectionTestUtils.setField(config, "idempotence", true);
    ReflectionTestUtils.setField(config, "retries", 3);
    ReflectionTestUtils.setField(config, "maxRequestsInFlight", 5);
    ReflectionTestUtils.setField(config, "trustedPackages", "net.wowdev.ecommerce.domain.events");
    return config;
  }

  @Test
  void configuresProducerWithReliableDeliveryProperties() {
    KafkaConfig config = configured();

    ProducerFactory<String, Object> producerFactory = config.producerFactory();
    Map<String, Object> properties = producerFactory.getConfigurationProperties();

    assertThat(properties).containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    assertThat(properties).containsEntry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    assertThat(properties).containsEntry(ProducerConfig.ACKS_CONFIG, "all");
    assertThat(properties).containsEntry(ProducerConfig.RETRIES_CONFIG, 3);
    assertThat(properties)
        .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    assertThat(properties)
        .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
  }

  @Test
  void configuresConsumerAndListenerErrorHandling() {
    KafkaConfig config = configured();
    ConsumerFactory<String, Object> consumerFactory = config.consumerFactory();
    Map<String, Object> properties = consumerFactory.getConfigurationProperties();
    KafkaTemplate<String, Object> template = config.kafkaTemplate(config.producerFactory());
    ConcurrentKafkaListenerContainerFactory<String, Object> listenerFactory =
        config.kafkaListenerContainerFactory(consumerFactory, template);

    assertThat(properties).containsEntry(ConsumerConfig.GROUP_ID_CONFIG, "customers");
    assertThat(properties)
        .containsEntry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    assertThat(properties)
        .containsEntry(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
    assertThat(properties)
        .containsEntry(
            JacksonJsonDeserializer.TRUSTED_PACKAGES, "net.wowdev.ecommerce.domain.events");
    assertThat(listenerFactory.getConsumerFactory()).isSameAs(consumerFactory);
    assertThat(ReflectionTestUtils.getField(listenerFactory, "concurrency")).isEqualTo(3);
    assertThat(ReflectionTestUtils.getField(listenerFactory, "commonErrorHandler"))
        .isInstanceOf(DefaultErrorHandler.class);
  }
}
