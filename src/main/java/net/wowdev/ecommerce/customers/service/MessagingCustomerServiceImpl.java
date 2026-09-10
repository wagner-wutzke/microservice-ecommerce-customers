package net.wowdev.ecommerce.customers.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.customers.messaging.CustomerProducer;
import net.wowdev.ecommerce.customers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.PaymentMethodDTO;
import net.wowdev.ecommerce.domain.events.*;
import net.wowdev.ecommerce.domain.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingCustomerServiceImpl implements MessagingCustomerService {

  private final CustomerProducer customerProducer;

  private final CustomerRepository repository;

  @Transactional
  @Override
  public void process(OrderCreated event) {
    UUID customerId = event.orderDTO().getCustomerId();
    try {
      CustomerDTO customerDTO =
          CustomerMapper.toDto(
              repository
                  .findById(customerId)
                  .orElseThrow(() -> new CustomerNotFoundException(customerId)));

      // TODO get the Payment Method entry marked as default
      this.publishPaymentMethodReplicationCompleted(event, customerDTO.getPaymentMethods().getFirst());

      // Do not expose payment details in the customer event.
      customerDTO.getPaymentMethods().clear();
      this.publishCustomerReplicationCompleted(event, customerDTO);
    } catch (Exception exception) {
      log.error(
          ">> Failed loading Customer record for id {}: {}", customerId, exception.getMessage());
      this.publishCustomerReplicationFailed(event, exception.getMessage());
    }
  }

  protected void publishCustomerReplicationCompleted(OrderCreated event, CustomerDTO customerDTO) {
    CustomerReplicationCompleted dataReplicationEvent =
        new CustomerReplicationCompleted(
            UUID.randomUUID(),
            event.transactionId(),
            event.orderDTO(),
            customerDTO,
            Instant.now(),
            ORIGIN_SERVICE);
    customerProducer.publish(dataReplicationEvent);
  }

  protected void publishCustomerReplicationFailed(OrderCreated event, String reason) {
    customerProducer.publish(
        new CustomerReplicationFailed(
            UUID.randomUUID(), event.transactionId(), null, reason, Instant.now(), ORIGIN_SERVICE));
  }

  protected void publishPaymentMethodReplicationCompleted(OrderCreated event, PaymentMethodDTO paymentMethodDTO) {
    PaymentMethodReplicationCompleted paymentMethodLoadedEvent =
        new PaymentMethodReplicationCompleted(
            UUID.randomUUID(),
            event.transactionId(),
            paymentMethodDTO,
            Instant.now(),
            ORIGIN_SERVICE);
    customerProducer.publish(paymentMethodLoadedEvent);
  }
}
