-- Seed only an empty customer database. H2 has no conditional TRUNCATE syntax;
-- this guarded delete leaves addresses empty exactly when customers is empty.
DELETE FROM addresses WHERE NOT EXISTS (SELECT 1 FROM customers);

INSERT INTO addresses (id, address_line_1, address_line_2, city, state_province, postal_code, country, created_at, modified_at)
SELECT CAST(seed.id AS UUID), seed.line_1, seed.line_2, seed.city, seed.state_province, seed.postal_code, seed.country,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('00000000-0000-0000-0000-000000000001', '14 Lantern Walk', 'North Wing', 'Portland', 'Oregon', '97205', 'USA'),
    ('00000000-0000-0000-0000-000000000002', '88 Paperbark Lane', 'Suite 4', 'Melbourne', 'Victoria', '3000', 'Australia'),
    ('00000000-0000-0000-0000-000000000003', '7 Copper Finch Road', 'Flat 2', 'Bristol', 'Somerset', 'BS1 4QA', 'UK'),
    ('00000000-0000-0000-0000-000000000004', '221 Cloudberry Street', 'Unit 8', 'Reykjavik', 'Capital Region', '101', 'Iceland'),
    ('00000000-0000-0000-0000-000000000005', '53 Juniper Avenue', 'House B', 'Wellington', 'Wellington', '6011', 'New Zealand'),
    ('00000000-0000-0000-0000-000000000006', '19 Saffron Crescent', 'Floor 3', 'Dublin', 'Leinster', 'D02 X285', 'Ireland'),
    ('00000000-0000-0000-0000-000000000007', '402 Blue Heron Way', 'Apt 11', 'Austin', 'Texas', '78701', 'USA'),
    ('00000000-0000-0000-0000-000000000008', '6 Wisteria Mews', 'Rear Cottage', 'Bath', 'Somerset', 'BA1 2NB', 'UK'),
    ('00000000-0000-0000-0000-000000000009', '73 Ember Street', 'Suite 10', 'Oslo', 'Oslo', '0150', 'Norway'),
    ('00000000-0000-0000-0000-00000000000a', '9 Mossy Oak Drive', 'Unit 5', 'Atlanta', 'Georgia', '30303', 'USA'),
    ('00000000-0000-0000-0000-00000000000b', '145 Silver Birch Road', 'Level 2', 'Toronto', 'Ontario', 'M5V 2T6', 'Canada'),
    ('00000000-0000-0000-0000-00000000000c', '31 Lavender Quay', 'Dock House', 'Cork', 'Munster', 'T12 K2P8', 'Ireland'),
    ('00000000-0000-0000-0000-00000000000d', '84 Starling Boulevard', 'Apt 4C', 'Chicago', 'Illinois', '60601', 'USA'),
    ('00000000-0000-0000-0000-00000000000e', '12 Rowan Terrace', 'Garden Level', 'Edinburgh', 'Scotland', 'EH1 1AA', 'UK'),
    ('00000000-0000-0000-0000-00000000000f', '67 Glasswing Lane', 'Unit 9', 'Vancouver', 'British Columbia', 'V6B 1A1', 'Canada'),
    ('00000000-0000-0000-0000-000000000010', '28 Wildflower Court', 'House 6', 'Denver', 'Colorado', '80202', 'USA'),
    ('00000000-0000-0000-0000-000000000011', '5 Moonstone Parade', 'Flat 1', 'Sydney', 'New South Wales', '2000', 'Australia'),
    ('00000000-0000-0000-0000-000000000012', '90 Hazelwood Park', 'Building C', 'Manchester', 'Lancashire', 'M1 1AE', 'UK'),
    ('00000000-0000-0000-0000-000000000013', '16 Cinder Lane', 'Apt 12', 'Helsinki', 'Uusimaa', '00100', 'Finland'),
    ('00000000-0000-0000-0000-000000000014', '304 Velvet Pine Street', 'Suite 7', 'San Diego', 'California', '92101', 'USA'),
    ('00000000-0000-0000-0000-000000000015', '41 Tumbleweed Road', 'Unit 3', 'Phoenix', 'Arizona', '85004', 'USA'),
    ('00000000-0000-0000-0000-000000000016', '8 Orchard Bell Close', 'House A', 'Leeds', 'Yorkshire', 'LS1 2AB', 'UK'),
    ('00000000-0000-0000-0000-000000000017', '59 Coral Fern Avenue', 'Level 1', 'Brisbane', 'Queensland', '4000', 'Australia'),
    ('00000000-0000-0000-0000-000000000018', '117 Alder Street', 'Suite 2', 'Montreal', 'Quebec', 'H2Y 1C6', 'Canada'),
    ('00000000-0000-0000-0000-000000000019', '3 Nightingale Row', 'Flat 5', 'Glasgow', 'Scotland', 'G1 1XW', 'UK'),
    ('00000000-0000-0000-0000-00000000001a', '76 Willowglass Road', 'Apt 14', 'Seattle', 'Washington', '98101', 'USA'),
    ('00000000-0000-0000-0000-00000000001b', '22 Kestrel Grove', 'Unit 2', 'Christchurch', 'Canterbury', '8011', 'New Zealand'),
    ('00000000-0000-0000-0000-00000000001c', '101 Marigold Street', 'Floor 4', 'Adelaide', 'South Australia', '5000', 'Australia'),
    ('00000000-0000-0000-0000-00000000001d', '48 Fable Crescent', 'House 9', 'Cardiff', 'Wales', 'CF10 1EP', 'UK'),
    ('00000000-0000-0000-0000-00000000001e', '11 Opal Crossing', 'Suite 1', 'Zurich', 'Zurich', '8001', 'Switzerland'),
    ('00000000-0000-0000-0000-00000000001f', '63 Birch Lantern Way', 'Apt 6', 'Boston', 'Massachusetts', '02108', 'USA'),
    ('00000000-0000-0000-0000-000000000020', '4 Pomegranate Street', 'Unit 12', 'Lisbon', 'Lisbon', '1100-001', 'Portugal'),
    ('00000000-0000-0000-0000-000000000021', '39 Indigo Vale', 'House 3', 'Auckland', 'Auckland', '1010', 'New Zealand'),
    ('00000000-0000-0000-0000-000000000022', '15 Lanternfish Road', 'Flat 7', 'Belfast', 'Ulster', 'BT1 1AA', 'UK'),
    ('00000000-0000-0000-0000-000000000023', '82 Meadowlark Drive', 'Suite 5', 'Nashville', 'Tennessee', '37219', 'USA'),
    ('00000000-0000-0000-0000-000000000024', '6 Eucalyptus Place', 'Apt 2', 'Perth', 'Western Australia', '6000', 'Australia'),
    ('00000000-0000-0000-0000-000000000025', '27 Fuchsia Lane', 'Unit 4', 'Copenhagen', 'Capital', '1050', 'Denmark'),
    ('00000000-0000-0000-0000-000000000026', '93 Hearthstone Boulevard', 'Floor 2', 'Prague', 'Prague', '110 00', 'Czechia'),
    ('00000000-0000-0000-0000-000000000027', '18 Seafoam Court', 'House 8', 'San Francisco', 'California', '94105', 'USA'),
    ('00000000-0000-0000-0000-000000000028', '70 Foxglove Crescent', 'Suite 6', 'Cape Town', 'Western Cape', '8001', 'South Africa')
) AS seed(id, line_1, line_2, city, state_province, postal_code, country)
WHERE NOT EXISTS (SELECT 1 FROM customers);

INSERT INTO customers (id, first_name, last_name, email, date_of_birth, status,
                       billing_address_id, shipping_address_id, created_at, modified_at)
SELECT CAST(seed.id AS UUID), seed.first_name, seed.last_name, seed.email, seed.date_of_birth, seed.status,
       CAST(seed.billing_id AS UUID), CAST(seed.shipping_id AS UUID), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('10000000-0000-0000-0000-000000000001', 'Zephyr', 'Quill', 'zephyr.quill@example.com', DATE '1987-02-14', 'ACTIVE', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002'),
    ('10000000-0000-0000-0000-000000000002', 'Maribel', 'Thornfield', 'maribel.thornfield@example.com', DATE '1992-11-03', 'ACTIVE', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004'),
    ('10000000-0000-0000-0000-000000000003', 'Orson', 'Vexley', 'orson.vexley@example.com', DATE '1978-06-27', 'SUSPENDED', '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000006'),
    ('10000000-0000-0000-0000-000000000004', 'Calista', 'Moonridge', 'calista.moonridge@example.com', DATE '1995-09-18', 'ACTIVE', '00000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000008'),
    ('10000000-0000-0000-0000-000000000005', 'Bram', 'Holloway', 'bram.holloway@example.com', DATE '1983-12-09', 'INACTIVE', '00000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-00000000000a'),
    ('10000000-0000-0000-0000-000000000006', 'Elowen', 'Cricket', 'elowen.cricket@example.com', DATE '1990-04-22', 'ACTIVE', '00000000-0000-0000-0000-00000000000b', '00000000-0000-0000-0000-00000000000c'),
    ('10000000-0000-0000-0000-000000000007', 'Rafferty', 'Nightingale', 'rafferty.nightingale@example.com', DATE '1975-08-31', 'ACTIVE', '00000000-0000-0000-0000-00000000000d', '00000000-0000-0000-0000-00000000000e'),
    ('10000000-0000-0000-0000-000000000008', 'Saskia', 'Pebbleton', 'saskia.pebbleton@example.com', DATE '1988-01-16', 'SUSPENDED', '00000000-0000-0000-0000-00000000000f', '00000000-0000-0000-0000-000000000010'),
    ('10000000-0000-0000-0000-000000000009', 'Dorian', 'Foxglove', 'dorian.foxglove@example.com', DATE '1997-10-05', 'ACTIVE', '00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000012'),
    ('10000000-0000-0000-0000-00000000000a', 'Imogen', 'Starling', 'imogen.starling@example.com', DATE '1981-03-29', 'ACTIVE', '00000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000014'),
    ('10000000-0000-0000-0000-00000000000b', 'Peregrine', 'Wick', 'peregrine.wick@example.com', DATE '1969-07-12', 'INACTIVE', '00000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000016'),
    ('10000000-0000-0000-0000-00000000000c', 'Juniper', 'Marlow', 'juniper.marlow@example.com', DATE '1993-05-24', 'ACTIVE', '00000000-0000-0000-0000-000000000017', '00000000-0000-0000-0000-000000000018'),
    ('10000000-0000-0000-0000-00000000000d', 'Caspian', 'Dapple', 'caspian.dapple@example.com', DATE '1986-11-19', 'ACTIVE', '00000000-0000-0000-0000-000000000019', '00000000-0000-0000-0000-00000000001a'),
    ('10000000-0000-0000-0000-00000000000e', 'Theodora', 'Bracken', 'theodora.bracken@example.com', DATE '1979-02-07', 'SUSPENDED', '00000000-0000-0000-0000-00000000001b', '00000000-0000-0000-0000-00000000001c'),
    ('10000000-0000-0000-0000-00000000000f', 'Lucian', 'Mossgrove', 'lucian.mossgrove@example.com', DATE '1991-08-13', 'ACTIVE', '00000000-0000-0000-0000-00000000001d', '00000000-0000-0000-0000-00000000001e'),
    ('10000000-0000-0000-0000-000000000010', 'Ophelia', 'Rookwood', 'ophelia.rookwood@example.com', DATE '1984-06-02', 'ACTIVE', '00000000-0000-0000-0000-00000000001f', '00000000-0000-0000-0000-000000000020'),
    ('10000000-0000-0000-0000-000000000011', 'Alistair', 'Quasar', 'alistair.quasar@example.com', DATE '1972-12-28', 'INACTIVE', '00000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000022'),
    ('10000000-0000-0000-0000-000000000012', 'Seraphina', 'Tumbleweed', 'seraphina.tumbleweed@example.com', DATE '1998-04-10', 'ACTIVE', '00000000-0000-0000-0000-000000000023', '00000000-0000-0000-0000-000000000024'),
    ('10000000-0000-0000-0000-000000000013', 'Magnus', 'Larkspur', 'magnus.larkspur@example.com', DATE '1980-09-26', 'ACTIVE', '00000000-0000-0000-0000-000000000025', '00000000-0000-0000-0000-000000000026'),
    ('10000000-0000-0000-0000-000000000014', 'Niamh', 'Cinderby', 'niamh.cinderby@example.com', DATE '1994-01-30', 'SUSPENDED', '00000000-0000-0000-0000-000000000027', '00000000-0000-0000-0000-000000000028')
) AS seed(id, first_name, last_name, email, date_of_birth, status, billing_id, shipping_id)
WHERE NOT EXISTS (SELECT 1 FROM customers);
