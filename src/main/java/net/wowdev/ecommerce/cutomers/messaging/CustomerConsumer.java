package net.wowdev.ecommerce.cutomers.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.cutomers.service.MessagingCustomerService;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "kafkaListenerContainerFactory",
    topics = {"${app.kafka.orders-topic}"})
@AllArgsConstructor
public class CustomerConsumer {

  private final MessagingCustomerService messagingCustomerService;

  @KafkaHandler
  public void handle(OrderCreatedEvent event) {
    log.debug(">> Processing OrderCreatedEvent:  {}", event.eventId());
    messagingCustomerService.handleOrderCreated(event);
  }
}
