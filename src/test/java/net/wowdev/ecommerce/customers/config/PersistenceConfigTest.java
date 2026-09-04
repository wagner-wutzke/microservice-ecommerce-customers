package net.wowdev.ecommerce.customers.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

class PersistenceConfigTest {
  @Test
  void enablesJpaAuditing() {
    assertThat(PersistenceConfig.class.getAnnotation(EnableJpaAuditing.class)).isNotNull();
    assertThat(new PersistenceConfig()).isNotNull();
  }
}
