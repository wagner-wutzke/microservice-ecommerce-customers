package net.wowdev.ecommerce.cutomers.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.cutomers.messaging.CustomerProducer;
import net.wowdev.ecommerce.cutomers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.PaymentMethodDTO;
import net.wowdev.ecommerce.domain.events.*;
import net.wowdev.ecommerce.domain.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultMessagingCustomerService implements MessagingCustomerService {

  private final CustomerProducer customerProducer;

  private final CustomerRepository repository;

  @Transactional(readOnly = true)
  @Override
  public void handleOrderCreated(OrderCreatedEvent event) {
    UUID customerId = event.orderDTO().getCustomerId();

    try {
      CustomerDTO customerDTO =
          CustomerMapper.toDto(
              repository
                  .findById(customerId)
                  .orElseThrow(() -> new CustomerNotFoundException(customerId)));

      // TODO get the Payment Method entry marked as default
      this.publishPaymentMethodLoaded(event, customerDTO.getPaymentMethods().getFirst());

      // Do not expose payment details in the customer event.
      customerDTO.getPaymentMethods().clear();
      this.publishCustomerLoaded(event, customerDTO);
      this.publishOrderProcessingStarted(event);
    } catch (Exception exception) {
      log.error(
          ">> Failed loading Customer record for id {}: {}", customerId, exception.getMessage());
      this.publishCustomerLoadingFailed(event, exception.getMessage());
    }
  }

  protected void publishOrderProcessingStarted(OrderCreatedEvent event) {
    OrderProcessingStartedEvent orderProcessingStartedEvent =
        new OrderProcessingStartedEvent(
            UUID.randomUUID(),
            event.transactionId(),
            event.orderDTO(),
            Instant.now(),
            ORIGIN_SERVICE);
    customerProducer.publish(orderProcessingStartedEvent);
  }

  protected void publishCustomerLoaded(OrderCreatedEvent event, CustomerDTO customerDTO) {
    CustomerLoadedEvent customerDataLoadedEvent =
        new CustomerLoadedEvent(
            UUID.randomUUID(), event.transactionId(), customerDTO, Instant.now(), ORIGIN_SERVICE);
    customerProducer.publish(customerDataLoadedEvent);
  }

  protected void publishCustomerLoadingFailed(OrderCreatedEvent event, String reason) {
    customerProducer.publish(
        new CustomerLoadingFailedEvent(
            UUID.randomUUID(), event.transactionId(), null, reason, Instant.now(), ORIGIN_SERVICE));
  }

  protected void publishPaymentMethodLoaded(
      OrderCreatedEvent event, PaymentMethodDTO paymentMethodDTO) {
    PaymentMethodLoadedEvent paymentMethodLoadedEvent =
        new PaymentMethodLoadedEvent(
            UUID.randomUUID(),
            event.transactionId(),
            paymentMethodDTO,
            Instant.now(),
            ORIGIN_SERVICE);
    customerProducer.publish(paymentMethodLoadedEvent);
  }
}
