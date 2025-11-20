-- ============================================
-- DEMO DATA for 'projects_test'
-- HARD CODED admin for login
-- ============================================

INSERT INTO `projects_test`
(`title`, `name`, `caldera`, `warranty`, `ssn`, `phone_num`, `address`, `email`, `hours`, `est_due_date`, `priority`, `description`, `status`, `sort_index`, `date`, `end_date`)
VALUES
    ('[DEMO] Stark Tower Heater Overhaul', 'Tony Stark', TRUE, TRUE, '060573-1111', '50110001', '200 Park Ave, New York', 'tony@starkindustries.com', 16, DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Red', 'Complete overhaul of tower’s Caldera heating system with arc reactor integration.', 'inProgress', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), null),

    ('[DEMO] Batcave Dehumidifier Replacement', 'Bruce Wayne', TRUE, FALSE, '190238-2222', '50110002', '1007 Mountain Dr, Gotham', 'bruce@wayneenterprises.com', 12, DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'Yellow', 'Replace dehumidifier system to prevent moisture damage in Batmobile area.', 'underReview', 2, DATE_SUB(CURDATE(), INTERVAL 12 DAY), null),

    ('[DEMO] Asgard Sauna Renovation', 'Thor Odinson', TRUE, TRUE, '050684-3333', '50110003', 'Valhalla 1, Asgard', 'thor@asgardrealm.org', 20, DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'Red', 'Rebuild royal sauna facility using lightning-resistant materials.', 'inProgress', 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), null),

    ('[DEMO] Atlantis Hydro Repair', 'Arthur Curry', FALSE, TRUE, '290180-4444', '50110004', 'Deep Sea 42, Atlantis', 'arthur@atlantis.gov', 10, DATE_ADD(CURDATE(), INTERVAL 8 DAY), 'Green', 'Inspect underwater pressure valves and replace corroded components.', 'billing', 4, DATE_SUB(CURDATE(), INTERVAL 28 DAY), null),

    ('[DEMO] X-Mansion Boiler Upgrade', 'Charles Xavier', TRUE, TRUE, '150950-5555', '50110005', '1407 Graymalkin Ln, Westchester', 'charles@xavierschool.edu', 18, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'Red', 'Upgrade the mansion’s boiler system for stable thermal regulation.', 'inProgress', 5, DATE_SUB(CURDATE(), INTERVAL 10 DAY), null),

    ('[DEMO] Daily Planet Lobby Fountain', 'Clark Kent', FALSE, FALSE, '180682-6666', '50110006', '355 Main St, Metropolis', 'clark@dailyplanet.com', 6, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Yellow', 'Repair lobby fountain leak and inspect water pressure regulators.', 'billing', 6, DATE_SUB(CURDATE(), INTERVAL 20 DAY), null),

    ('[DEMO] Wakanda Cooling Chamber', 'Shuri', TRUE, TRUE, '030598-7777', '50110007', 'Royal Tech Labs, Wakanda', 'shuri@wakanda.gov', 15, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'Red', 'Install vibranium-lined cooling chamber for energy experiments.', 'inProgress', 7, DATE_SUB(CURDATE(), INTERVAL 7 DAY), null),

    ('[DEMO] Central City Spa Wiring', 'Barry Allen', TRUE, FALSE, '010392-8888', '50110008', '101 Flash Ave, Central City', 'barry@ccpd.gov', 7, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'Yellow', 'Fix faulty wiring in spa heating system. Ensure safety at super-speed.', 'underReview', 8, DATE_SUB(CURDATE(), INTERVAL 14 DAY), null),

    ('[DEMO] Hell’s Kitchen Hot Tub', 'Matt Murdock', FALSE, FALSE, '150284-9999', '50110009', 'Nelson & Murdock LLP, Hell’s Kitchen', 'matt@nmfirm.com', 5, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Green', 'Diagnose temperature drop in rooftop hot tub. Warranty expired.', 'billing', 9, DATE_SUB(CURDATE(), INTERVAL 18 DAY), null),

    ('[DEMO] Gotham Police Sauna Maintenance', 'Jim Gordon', TRUE, TRUE, '221160-1212', '50110010', '1 GCPD Plaza, Gotham', 'jim.gordon@gcpd.gov', 8, DATE_ADD(CURDATE(), INTERVAL 11 DAY), 'Yellow', 'Service precinct sauna and replace ventilation fans.', 'inProgress', 10, DATE_SUB(CURDATE(), INTERVAL 9 DAY), null),

    ('[DEMO] Sanctum Sanctorum Pipe Alignment', 'Stephen Strange', TRUE, TRUE, '100272-1313', '50110011', '177A Bleecker St, New York', 'stephen@sanctum.com', 9, DATE_ADD(CURDATE(), INTERVAL 13 DAY), 'Red', 'Align mystical water flow conduits. Avoid dimensional leaks.', 'underReview', 11, DATE_SUB(CURDATE(), INTERVAL 22 DAY), null),

    ('[DEMO] Arkham Asylum Boiler Maintenance', 'Harleen Quinzel', TRUE, FALSE, '200789-1414', '50110012', '12 Asylum Rd, Gotham', 'harleen@arkham.org', 11, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Yellow', 'Perform maintenance on boiler system in therapy wing.', 'billing', 12, DATE_SUB(CURDATE(), INTERVAL 16 DAY), null),

    ('[DEMO] Stark Industries Pool Filter', 'Pepper Potts', FALSE, TRUE, '130684-1515', '50110013', '10880 Malibu Point, California', 'pepper@starkindustries.com', 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Green', 'Replace defective filter in outdoor infinity pool.', 'billing', 13, DATE_SUB(CURDATE(), INTERVAL 4 DAY), null),

    ('[DEMO] Themyscira Thermal Baths', 'Diana Prince', TRUE, TRUE, '050372-1616', '50110014', 'Temple District, Themyscira', 'diana@amazonwarriors.org', 13, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'Red', 'Repair and reinforce stonework in royal thermal baths.', 'inProgress', 14, DATE_SUB(CURDATE(), INTERVAL 11 DAY), null),

    ('[DEMO] Queens Neighborhood Spa Setup', 'Peter Parker', FALSE, FALSE, '100198-1717', '50110015', '20 Ingram St, Forest Hills', 'peter@dailybugle.com', 6, DATE_ADD(CURDATE(), INTERVAL 9 DAY), 'Green', 'Install new home spa system for Aunt May.', 'underReview', 15, DATE_SUB(CURDATE(), INTERVAL 25 DAY), null),

    ('[DEMO - ARCHIVE] DEATHSTAR - New hot tub', 'Darth Vader', FALSE, TRUE, '261277-8891', '50120031', 'Space above geonosis', 'darthvader@deathstar.com', 4, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'Green', 'Lord Vader demands a new hot tub', 'closed', 22, DATE_SUB(CURDATE(), INTERVAL 14 DAY), '2025-11-03'),

    ('[DEMO - ARCHIVE] Starfleet Academy leakage', 'Jean-Luc Picard', FALSE, FALSE, '400175-5520', '50140011', '1 Starfleet Plaza, San Francisco', 'picard@starfleet.org', 3, DATE_ADD(CURDATE(), INTERVAL 18 DAY), 'Yellow', 'The starfleets hot tub has been leaking', 'closed', 10, DATE_SUB(CURDATE(), INTERVAL 45 DAY), '2025-08-10'),

    ('[DEMO - ARCHIVE] Jurassic Park T-REX tub', 'Ellie Sattler', TRUE, TRUE, '500333-9910', '50150026', 'Isla Nublar Research Site', 'ellie@jurassic.org', 7, DATE_ADD(CURDATE(), INTERVAL 22 DAY), 'Yellow', 'The T-REX need a new hot tub.', 'closed', 18, DATE_SUB(CURDATE(), INTERVAL 11 DAY), '2025-12-01'),

    ('[DEMO - ARCHIVE] Hogwarts magic hot tub', 'Hermione Granger', FALSE, FALSE, '600872-6633', '50160084', 'Hogwarts Castle, Scotland', 'hermione@hogwarts.edu', 5, DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Green', 'Hermione requests a new magic hot tub.', 'closed', 27, DATE_SUB(CURDATE(), INTERVAL 28 DAY), '2025-10-05');

INSERT INTO `appuser` (`id`, `password`, `role`, `username`, `name`)
VALUES('1', '$2a$12$Z7K.6g.MwiEAR.p.Le03I.7gftFgzAhjwVVeyZIsTHxBXXyRkA4Vu', 'ADMIN', 'admin', 'admin');
