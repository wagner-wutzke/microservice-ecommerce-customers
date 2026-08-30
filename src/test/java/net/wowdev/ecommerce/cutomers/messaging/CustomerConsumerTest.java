package net.wowdev.ecommerce.cutomers.messaging;

import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerConsumerTest {
    @Mock
    CustomerService customerService;
    @Test
    void consumesEventAndNotifiesSuccess() {
        final UUID customerId = UUID.randomUUID();
        final OrderDTO order = new OrderDTO();
        order.setCustomerId(customerId);
        when(customerService.findById(customerId)).thenReturn(new CustomerDTO());

        assertDoesNotThrow(() -> {
            new CustomerConsumer(customerService)
                    .handleOrderCreated(new OrderProcessingStartedEvent(
                            UUID.randomUUID(),
                            "TX_ID",
                            order,
                            Instant.now()
                    ));
        });
        verify(customerService).notifyLoadDataSucceeded(any(OrderProcessingStartedEvent.class), any(CustomerDTO.class));
    }

    @Test
    void notifiesFailureWhenCustomerLookupFails() {
        final UUID customerId = UUID.randomUUID();
        final OrderDTO order = new OrderDTO();
        order.setCustomerId(customerId);
        when(customerService.findById(customerId)).thenThrow(new RuntimeException("customer unavailable"));
        final OrderProcessingStartedEvent event = new OrderProcessingStartedEvent(
                UUID.randomUUID(), "TX_ID", order, Instant.now());

        assertDoesNotThrow(() -> new CustomerConsumer(customerService).handleOrderCreated(event));

        verify(customerService).notifyLoadDataFailed(event, null, "customer unavailable");
    }
}
