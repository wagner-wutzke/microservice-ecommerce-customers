---
name: create-kafka-rest-microservice
description:
---

- Based on the CustomerEntity class, create a start script for inserting 20 rows in the H2 DB,
  when starting the application.
- For each `customer` row, create and insert:
    - addresses must be located in Brazil
    - the customer id must be set with following pattern `5%07d-5%03d-5%03d-5%03d-5%07d`.
    - a new related row in the `payment_methods` table according to `PaymentMethodEntity`.
- Insert new `customers` only if the `customers` table is empty.