package net.wowdev.ecommerce.customers.service;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.springframework.data.domain.Page;

public interface CrudCustomerService {

  CustomerDTO findById(UUID id);

  Page<CustomerDTO> findAll(int page, int pageSize);

  CustomerDTO create(CustomerDTO customer);

  CustomerDTO update(UUID id, CustomerDTO customer);

  void delete(UUID id);
}
