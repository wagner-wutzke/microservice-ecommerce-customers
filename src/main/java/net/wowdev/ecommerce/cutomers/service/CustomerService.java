package net.wowdev.ecommerce.cutomers.service;

import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CustomerService {

    CustomerDTO findById(UUID id);

    Page<CustomerDTO> findAll(int page, int pageSize);

    CustomerDTO create(CustomerDTO customer);

    CustomerDTO update(UUID id, CustomerDTO customer);

    void delete(UUID id);
}
