package net.wowdev.ecommerce.customers.service;

import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;

public interface MessagingCustomerService {

  String ORIGIN_SERVICE = "CUSTOMERS-SERVICE";

  void process(OrderCreatedEvent event);
}
