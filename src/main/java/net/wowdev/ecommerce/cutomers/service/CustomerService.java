package net.wowdev.ecommerce.cutomers.service;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import org.springframework.data.domain.Page;

public interface CustomerService {

  CustomerDTO findById(UUID id);

  Page<CustomerDTO> findAll(int page, int pageSize);

  CustomerDTO create(CustomerDTO customer);

  CustomerDTO update(UUID id, CustomerDTO customer);

  void delete(UUID id);

  void handleOrderProcessingStarted(OrderProcessingStartedEvent event);
}
