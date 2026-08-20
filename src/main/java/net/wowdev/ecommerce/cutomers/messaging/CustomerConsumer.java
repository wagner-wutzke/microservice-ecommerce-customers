package net.wowdev.ecommerce.cutomers.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerConsumer {
    @KafkaListener(topics = "${app.kafka.customer-changes-topic}", containerFactory = "customerKafkaListenerContainerFactory")
    public void consume(final CustomerDTO customer) {
        log.info("Consumed customer change event with id {}", customer.getId());
    }
}
