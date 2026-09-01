package net.wowdev.ecommerce.cutomers.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "kafkaListenerContainerFactory",
    topics = "${app.kafka.order-events-topic}")
@AllArgsConstructor
public class CustomerConsumer {

  private final CustomerService customerService;

  @KafkaHandler
  public void handleOrderProcessingStarted(OrderProcessingStartedEvent event) {
    log.debug(">>>> Processing OrderProcessingStartedEvent...");
    customerService.handleOrderProcessingStarted(event);
  }
}
