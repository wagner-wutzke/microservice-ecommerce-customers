package net.wowdev.ecommerce.cutomers.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
@KafkaListener(
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory",
        topics = "${app.kafka.order-events-topic}"
)
@AllArgsConstructor
public class CustomerConsumer {

    private final CustomerService customerService;

    @KafkaHandler
    public void handleOrderCreated(OrderProcessingStartedEvent event) {
        log.debug(">>>> Processing OrderProcessingStartedEvent: {}", event);
        UUID customerId = event.orderDTO().getCustomerId();
        try {
            CustomerDTO customerDTO = customerService.findById(customerId);
            log.debug(">>>> Loaded CustomerDTO: {}", customerDTO);
            customerService.notifyLoadDataSucceeded(event, customerDTO);

        } catch (Exception e) {
            log.error(">>>> Failed loading CustomerDTO: {}", e.getMessage());
            customerService.notifyLoadDataFailed(event, null, e.getMessage());
        }
    }
}
