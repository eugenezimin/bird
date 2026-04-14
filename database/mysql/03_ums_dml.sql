-- ================================================================
-- FILE: 03_ums_dml.sql
-- Usage: mysql -u root -p < 03_ums_dml.sql
-- Run AFTER 01_ums_ddl.sql
-- ================================================================

USE `ums`;

-- ----------------------------------------------------------------
-- Roles
-- ----------------------------------------------------------------
INSERT INTO `roles` (`id`, `name`, `description`) VALUES
  (UUID_TO_BIN('43dcf1d6-3ac0-4290-99fd-44f9b9f9becf'), 'ADMIN',      'Administrative role'),
  (UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c'), 'SUBSCRIBER', 'Message content consumer'),
  (UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984'), 'PRODUCER',   'Message content producer');

-- ----------------------------------------------------------------
-- Users
-- password_hash values below are bcrypt placeholders — replace
-- with real hashes before deploying to any real environment.
-- ----------------------------------------------------------------
INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `created_at`) VALUES
  -- Original users
  (UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), 'Angela Merkel',      'angela@merkel.de',      '$2a$12$placeholder', '2017-09-01 10:00:00'),
  (UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), 'Emmanuel Macron',    'emmanuel@macron.fr',    '$2a$12$placeholder', '2017-09-01 10:05:00'),
  (UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'Donald Trump',       'donald@trump.us',       '$2a$12$placeholder', '2020-07-31 08:00:00'),
  (UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), 'Justin Trudeau',     'justin@trudeau.ca',     '$2a$12$placeholder', '2017-09-01 10:10:00'),
  (UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), 'Vladimir Putin',     'vladimir@putin.ru',     '$2a$12$placeholder', '2017-09-01 10:15:00'),
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), 'Olaf Scholz',        'olaf@scholz.de',        '$2a$12$placeholder', '2021-12-08 09:00:00'),
  (UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), 'Giorgia Meloni',     'giorgia@meloni.it',     '$2a$12$placeholder', '2022-10-22 09:00:00'),
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), 'Rishi Sunak',        'rishi@sunak.uk',        '$2a$12$placeholder', '2022-10-25 09:00:00'),
  (UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), 'Ursula von der Leyen','ursula@vonderleyen.eu',  '$2a$12$placeholder', '2019-12-01 09:00:00'),
  (UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), 'Narendra Modi',      'narendra@modi.in',      '$2a$12$placeholder', '2014-05-26 09:00:00'),
  (UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), 'Xi Jinping',         'xi@jinping.cn',        '$2a$12$placeholder', '2013-03-14 09:00:00');

-- ----------------------------------------------------------------
-- Users <-> Roles
-- ----------------------------------------------------------------
INSERT INTO `users_roles` (`user_id`, `role_id`) VALUES
  -- Angela Merkel: SUBSCRIBER
  (UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  -- Emmanuel Macron: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Donald Trump: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Justin Trudeau: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Vladimir Putin: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Olaf Scholz: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Giorgia Meloni: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Rishi Sunak: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Ursula von der Leyen: SUBSCRIBER + PRODUCER + ADMIN
  (UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), UUID_TO_BIN('43dcf1d6-3ac0-4290-99fd-44f9b9f9becf')),
  (UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Narendra Modi: SUBSCRIBER + PRODUCER
  (UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c')),
  (UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), UUID_TO_BIN('eb932dbb-7005-422f-a649-7190af39e984')),
  -- Xi Jinping: SUBSCRIBER only
  (UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), UUID_TO_BIN('b479b357-7e25-47fa-8dba-bfdaeecc6c2c'));

-- ----------------------------------------------------------------
-- Sessions (sample login history)
-- ----------------------------------------------------------------
INSERT INTO `sessions` (`id`, `user_id`, `logged_in_at`, `logged_out_at`) VALUES
  (UUID_TO_BIN('56de00c1-a7dd-4f66-9728-307c5230455e'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), '2020-11-12 13:55:00', '2020-11-12 15:00:00'),
  (UUID_TO_BIN('e2f86261-b47a-4af3-8fc8-1f3ca6974922'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2020-11-12 14:10:00', '2020-11-12 16:30:00'),
  (UUID_TO_BIN('5d91940b-2c26-4e06-bab4-425369823962'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), '2020-11-12 14:00:00', '2020-11-12 14:45:00'),
  (UUID_TO_BIN('32827397-079c-4f06-a984-fe7df98bfbe0'), UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), '2020-11-12 09:00:00', '2020-11-12 09:30:00'),
  (UUID_TO_BIN('c44c66ce-875f-4565-a4b4-95b3c3a866c3'), UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), '2022-03-01 06:00:00', NULL);