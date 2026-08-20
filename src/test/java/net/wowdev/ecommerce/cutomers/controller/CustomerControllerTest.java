package net.wowdev.ecommerce.cutomers.controller;

import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class CustomerControllerTest {
    private final CustomerService service = mock(CustomerService.class);
    private final CustomerController controller = new CustomerController(service);
    private final UUID id = UUID.randomUUID();

    @Test
    void delegatesCrudOperations() {
        final CustomerDTO dto = new CustomerDTO();
        dto.setId(id);
        when(service.findById(id)).thenReturn(dto);
        when(service.create(dto)).thenReturn(dto);
        when(service.update(id, dto)).thenReturn(dto);
        when(service.findAll(0, 20)).thenReturn(new PageImpl<>(List.of(dto)));
        assertSame(dto, controller.findById(id));
        assertEquals(1, controller.findAll(0, 20).getTotalElements());
        assertEquals(201, controller.create(dto).getStatusCode().value());
        assertSame(dto, controller.update(id, dto));
        controller.delete(id);
        verify(service).delete(id);
    }
}
