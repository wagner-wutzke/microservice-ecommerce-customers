---
name: create-kafka-rest-microservice
description:
---

- Based on the CustomerEntity class, create a start script for inserting 20 rows in the H2 DB, 
when starting the application.
- For each `customer` row, create and insert:
  - one billing address in Brazil
  - one shipping address in Brazil
- For each address row, the respective customer id must be set.
- Configure the script to insert the new `address` rows at once, before inserting customers.
- Insert new `customers` and `addresses` only if the `customers` table is empty.
- Truncate the `addresses` table before inserting new customers via script.