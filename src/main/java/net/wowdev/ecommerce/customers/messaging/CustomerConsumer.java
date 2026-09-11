package net.wowdev.ecommerce.customers.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.customers.service.MessagingCustomerService;
import net.wowdev.ecommerce.domain.events.CustomerReplicationRequested;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "kafkaListenerContainerFactory",
    topics = { "${app.kafka.orders-topic}", "${app.kafka.customers-topic}" })
@AllArgsConstructor
public class CustomerConsumer {

  private final MessagingCustomerService service;

  @KafkaHandler
  public void handle(CustomerReplicationRequested event) {
    log.debug(
        ">> Processing CustomerReplicationRequested event from {}. EventId: {}", event.origin(), event.eventId());
    service.process(event);
  }

  @KafkaHandler(isDefault = true)
  public void handleUnknown(Object event) {
    //log.debug(">> Received an unmapped event of type {}", event.getClass().getSimpleName());
  }
}
