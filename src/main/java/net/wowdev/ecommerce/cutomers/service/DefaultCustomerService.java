package net.wowdev.ecommerce.cutomers.service;

import lombok.RequiredArgsConstructor;
import net.wowdev.ecommerce.cutomers.messaging.CustomerProducer;
import net.wowdev.ecommerce.cutomers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import net.wowdev.ecommerce.domain.events.CustomerDataFailedEvent;
import net.wowdev.ecommerce.domain.events.CustomerDataLoadedEvent;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import net.wowdev.ecommerce.domain.mapper.CustomerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultCustomerService implements CustomerService {

    private final CustomerRepository repository;

    private final CustomerProducer customerProducer;

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findById(final UUID id) {
        return CustomerMapper.toDto(repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(final int page, final int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(CustomerMapper::toDto);
    }

    @Override
    @Transactional
    public CustomerDTO create(final CustomerDTO customer) {
        final Instant now = Instant.now();
        final CustomerEntity entity = CustomerMapper.toEntity(customer);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(now);
        entity.setModifiedAt(now);
        return CustomerMapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public CustomerDTO update(final UUID id, final CustomerDTO customer) {
        final CustomerEntity entity = repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
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

    @Override
    public void notifyLoadDataSucceeded(OrderProcessingStartedEvent event, CustomerDTO customerDTO) {
        CustomerDataLoadedEvent customerDataLoadedEvent = new CustomerDataLoadedEvent(
                UUID.randomUUID(),
                event.transactionId(),
                customerDTO,
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
        customerProducer.publish(customerDataLoadedEvent);
    }

    @Override
    public void notifyLoadDataFailed(OrderProcessingStartedEvent event, CustomerDTO customerDTO, String reason) {
        customerProducer.publish(new CustomerDataFailedEvent(
                UUID.randomUUID(),
                event.transactionId(),
                null,
                reason,
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));
    }
}
