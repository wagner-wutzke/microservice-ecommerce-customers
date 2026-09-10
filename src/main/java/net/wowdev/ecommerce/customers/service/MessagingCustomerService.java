package net.wowdev.ecommerce.customers.service;

import net.wowdev.ecommerce.domain.events.OrderCreated;

public interface MessagingCustomerService {

  String ORIGIN_SERVICE = "CUSTOMERS-SERVICE";

  void process(OrderCreated event);
}
