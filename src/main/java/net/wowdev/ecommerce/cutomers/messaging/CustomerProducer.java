package net.wowdev.ecommerce.cutomers.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.CustomerDataLoadedEvent;
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
            @Value("${app.kafka.customer-events-topic}") final String customerEventsTopic
    ) {
        this.template = template;
        this.customerEventsTopic = customerEventsTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final CustomerDataLoadedEvent event) {
        log.info(">>>> Publishing CustomerDataLoadedEvent: {}", event);
        template.send(customerEventsTopic, event.eventId().toString(), event);
    }
}
