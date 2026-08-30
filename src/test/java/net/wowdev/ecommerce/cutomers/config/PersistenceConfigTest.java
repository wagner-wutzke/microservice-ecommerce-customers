package net.wowdev.ecommerce.cutomers.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceConfigTest {
    @Test
    void createsPersistenceConfiguration() {
        assertThat(new PersistenceConfig()).isNotNull();
    }
}
