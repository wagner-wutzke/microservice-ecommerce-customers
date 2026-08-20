package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CustomerProducer {
    private final KafkaTemplate<String, CustomerDTO> template;
    private final String topic;

    public CustomerProducer(final KafkaTemplate<String, CustomerDTO> template,
                            @Value("${app.kafka.customer-changes-topic}") final String topic) {
        this.template = template;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final CustomerDTO customer) {
        template.send(topic, customer.getId().toString(), customer);
    }
}
