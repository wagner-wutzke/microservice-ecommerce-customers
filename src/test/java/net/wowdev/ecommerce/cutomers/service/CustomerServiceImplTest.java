package net.wowdev.ecommerce.cutomers.service;

import net.wowdev.ecommerce.cutomers.repository.CustomerRepository;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import net.wowdev.ecommerce.domain.enums.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    CustomerRepository repository;
    @Mock
    ApplicationEventPublisher events;
    @InjectMocks
    DefaultCustomerService service;

    private static CustomerDTO dto() {
        final CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Ada");
        dto.setLastName("Lovelace");
        dto.setEmail("ada@example.com");
        dto.setCustomerStatus(CustomerStatus.ACTIVE);
        return dto;
    }

    private static CustomerEntity entity(final UUID id) {
        return new CustomerEntity(id, "Ada", "Lovelace", "ada@example.com",
                CustomerStatus.ACTIVE, List.of(), "addressLine1", "addressLine2",
                "city", "province", "postalCode", "country",
                java.time.Instant.now(), java.time.Instant.now());
    }

    @Test
    void findsAndPagesCustomers() {
        final UUID id = UUID.randomUUID();
        final CustomerEntity entity = entity(id);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(entity)));
        assertEquals(id, service.findById(id).getId());
        assertEquals(1, service.findAll(0, 20).getTotalElements());
    }

    @Test
    void createsAndUpdatesCustomer() {
        final CustomerDTO input = dto();
        final CustomerEntity saved = entity(UUID.randomUUID());
        when(repository.save(any(CustomerEntity.class))).thenReturn(saved);
        final CustomerDTO created = service.create(input);
        assertNotNull(created.getId());
        when(repository.findById(saved.getId())).thenReturn(Optional.of(saved));
        final CustomerDTO updated = service.update(saved.getId(), input);
        assertEquals(saved.getId(), updated.getId());
        verify(events, times(2)).publishEvent(any(CustomerDTO.class));
    }

    @Test
    void deletesAndRejectsMissingCustomers() {
        final UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);
        service.delete(id);
        verify(repository).deleteById(id);
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> service.findById(id));
        when(repository.existsById(id)).thenReturn(false);
        assertThrows(CustomerNotFoundException.class, () -> service.delete(id));
    }
}
