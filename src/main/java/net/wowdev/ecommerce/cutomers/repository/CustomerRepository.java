package net.wowdev.ecommerce.cutomers.repository;

import java.util.UUID;
import net.wowdev.ecommerce.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {}
