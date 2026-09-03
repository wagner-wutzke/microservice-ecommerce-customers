package net.wowdev.ecommerce.cutomers.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.cutomers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import net.wowdev.ecommerce.domain.events.*;
import net.wowdev.ecommerce.domain.mapper.CustomerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCrudCustomerService implements CrudCustomerService {

  private final CustomerRepository repository;

  @Override
  @Transactional(readOnly = true)
  public CustomerDTO findById(final UUID id) {
    return CustomerMapper.toDto(
        repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id)));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CustomerDTO> findAll(final int page, final int pageSize) {
    return repository
        .findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")))
        .map(CustomerMapper::toDto);
  }

  @Override
  @Transactional
  public CustomerDTO create(final CustomerDTO customer) {
    final UUID customerId = UUID.randomUUID();
    customer.setId(customerId);
    customer
        .getPaymentMethods()
        .forEach(
            (paymentMethod) -> {
              final UUID paymentMethodId = UUID.randomUUID();
              paymentMethod.setCustomerId(customerId);
              paymentMethod.setId(paymentMethodId);
            });
    final CustomerEntity entity = CustomerMapper.toEntity(customer);

    return CustomerMapper.toDto(repository.save(entity));
  }

  @Override
  @Transactional
  public CustomerDTO update(final UUID id, final CustomerDTO customer) {
    final CustomerEntity entity =
        repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    entity.setFirstName(customer.getFirstName());
    entity.setLastName(customer.getLastName());
    entity.setEmail(customer.getEmail());
    entity.setCustomerStatus(customer.getCustomerStatus());
    entity.setAddressLine1(customer.getAddressLine1());
    entity.setAddressLine2(customer.getAddressLine2());
    entity.setCity(customer.getCity());
    entity.setCountry(customer.getCountry());
    entity.setPostalCode(customer.getPostalCode());
    entity.setStateProvince(customer.getStateProvince());
    entity.setModifiedAt(Instant.now());
    return CustomerMapper.toDto(repository.save(entity));
  }

  @Override
  @Transactional
  public void delete(final UUID id) {
    if (!repository.existsById(id)) throw new CustomerNotFoundException(id);
    repository.deleteById(id);
  }
}
