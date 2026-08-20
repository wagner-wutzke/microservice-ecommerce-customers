package net.wowdev.ecommerce.cutomers.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import net.wowdev.ecommerce.cutomers.service.CustomerService;
import net.wowdev.ecommerce.domain.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(final CustomerService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public CustomerDTO findById(@PathVariable final UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public Page<CustomerDTO> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero final int page,
                                     @RequestParam(defaultValue = "20") @Positive final int pageSize) {
        return service.findAll(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody final CustomerDTO customer) {
        final CustomerDTO result = service.create(customer);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + result.getId())).body(result);
    }

    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable final UUID id, @Valid @RequestBody final CustomerDTO customer) {
        return service.update(id, customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
