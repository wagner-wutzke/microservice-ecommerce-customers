package net.wowdev.ecommerce.cutomers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "net.wowdev.ecommerce.domain.entity")
public class CustomersServiceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(CustomersServiceApplication.class, args);
    }
}
