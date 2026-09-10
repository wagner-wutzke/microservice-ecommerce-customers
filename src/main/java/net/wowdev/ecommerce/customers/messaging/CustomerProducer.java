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
  private final String topic;

  public CustomerProducer(
      final KafkaTemplate<String, Object> template,
      @Value("${app.kafka.customers-topic}") final String topic) {
    this.template = template;
    this.topic = topic;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final CustomerReplicationCompleted event) {
    log.debug(">> Publishing CustomerReplicationCompleted: {}", event.eventId());
    template.send(topic, event.transactionId(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final CustomerReplicationFailed event) {
    log.debug(">> Publishing CustomerReplicationFailed: {}", event.eventId());
    template.send(topic, event.transactionId(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final PaymentMethodReplicationCompleted event) {
    log.debug(">> Publishing PaymentMethodReplicationCompleted: {}", event.eventId());
    template.send(topic, event.transactionId(), event);
  }

}
