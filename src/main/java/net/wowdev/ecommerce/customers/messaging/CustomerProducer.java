package net.wowdev.ecommerce.customers.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class CustomerProducer {

  private final KafkaTemplate<String, Object> template;
  private final String customerEventsTopic;

  public CustomerProducer(
      final KafkaTemplate<String, Object> template,
      @Value("${app.kafka.customers-topic}") final String customerEventsTopic) {
    this.template = template;
    this.customerEventsTopic = customerEventsTopic;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final CustomerLoadedEvent event) {
    log.debug(">> Publishing CustomerLoadedEvent: {}", event.eventId());
    template.send(customerEventsTopic, event.transactionId(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final CustomerLoadingFailedEvent event) {
    log.debug(">> Publishing CustomerLoadingFailedEvent: {}", event.eventId());
    template.send(customerEventsTopic, event.transactionId(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final PaymentMethodLoadedEvent event) {
    log.debug(">> Publishing PaymentMethodLoadedEvent: {}", event.eventId());
    template.send(customerEventsTopic, event.transactionId(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(OrderProcessingStartedEvent event) {
    log.debug(">> Publishing OrderProcessingStartedEvent: {}", event.eventId());
    template.send(customerEventsTopic, event.transactionId(), event);
  }
}
