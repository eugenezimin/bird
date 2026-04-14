-- ================================================================
-- FILE: 04_twitter_dml.sql
-- Usage: mysql -u root -p < 04_twitter_dml.sql
-- Run AFTER 02_twitter_ddl.sql and 03_ums_dml.sql
-- ================================================================

USE `twitter`;

-- ----------------------------------------------------------------
-- Messages
-- ----------------------------------------------------------------
INSERT INTO `messages` (`id`, `author_id`, `content`, `created_at`) VALUES
  -- Donald Trump (6e27ea06)
  (UUID_TO_BIN('e5c6f13a-76d6-48b6-81f0-20b74fa9f04c'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'Donald Trump posted his first message',              '2020-11-12 14:38:29'),
  (UUID_TO_BIN('b7a1c8d5-c04e-4fe3-8f98-6392bb751ad9'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'And here Mr. Trump comes again',                     '2020-11-12 14:39:07'),
  (UUID_TO_BIN('462894b2-b5a1-4088-b7af-701c71d6d304'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'Who is subscribed to Mr. Trump?',                    '2020-11-12 14:39:29'),
  (UUID_TO_BIN('a1b2c3d4-e5f6-7890-ab12-cd34ef567890'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'MAKE AMERICA GREAT AGAIN!',                          '2020-11-13 08:15:00'),
  (UUID_TO_BIN('b2c3d4e5-f6a7-8901-bc23-de45fa678901'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), 'The economy has never been stronger.',               '2020-11-14 09:00:00'),
  -- Emmanuel Macron (1cd89e11)
  (UUID_TO_BIN('fccc455b-ff28-4dfd-8153-f07c0f869118'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), 'Now France President is here as well',               '2020-11-12 15:22:03'),
  (UUID_TO_BIN('df7e3d8f-e19a-4458-bad6-4be7585200f5'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), 'Mr. Macron would like to say hello!',                '2020-11-12 15:53:57'),
  (UUID_TO_BIN('c3d4e5f6-a7b8-9012-cd34-ef56ab789012'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), 'Vive la France! Vive la Republique!',                '2020-11-13 10:30:00'),
  (UUID_TO_BIN('d4e5f6a7-b8c9-0123-de45-fa67bc890123'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), 'Europe must stand united on climate change.',        '2020-11-14 11:00:00'),
  -- Justin Trudeau (70a64b54)
  (UUID_TO_BIN('e5f6a7b8-c9d0-1234-ef56-ab78cd901234'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), 'Greetings from Canada! Diversity is our strength.',  '2020-11-12 16:00:00'),
  (UUID_TO_BIN('f6a7b8c9-d0e1-2345-fa67-bc89de012345'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), 'Canada will always stand up for human rights.',      '2020-11-13 09:00:00'),
  (UUID_TO_BIN('a7b8c9d0-e1f2-3456-ab78-cd90ef123456'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), 'Proud to represent Canadians on the world stage.',   '2020-11-14 14:00:00'),
  -- Vladimir Putin (abb04b9f)
  (UUID_TO_BIN('b8c9d0e1-f2a3-4567-bc89-de01fa234567'), UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), 'Russia is strong and will remain strong.',           '2020-11-12 17:00:00'),
  (UUID_TO_BIN('c9d0e1f2-a3b4-5678-cd90-ef12ab345678'), UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), 'We will protect our sovereignty at all costs.',      '2020-11-13 08:00:00'),
  -- Olaf Scholz (c1a2b3c4)
  (UUID_TO_BIN('d0e1f2a3-b4c5-6789-de01-fa23bc456789'), UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), 'Germany stands with its European partners.',        '2021-12-09 10:00:00'),
  (UUID_TO_BIN('e1f2a3b4-c5d6-7890-ef12-ab34cd567890'), UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), 'Social democracy is the answer to modern challenges.','2021-12-10 11:00:00'),
  -- Giorgia Meloni (d2b3c4d5)
  (UUID_TO_BIN('f2a3b4c5-d6e7-8901-fa23-bc45de678901'), UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), 'Italy first. Always.',                               '2022-10-23 09:30:00'),
  (UUID_TO_BIN('a3b4c5d6-e7f8-9012-ab34-cd56ef789012'), UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), 'We will restore Italian pride and tradition.',       '2022-10-24 10:00:00'),
  -- Narendra Modi (b6f7a8b9)
  (UUID_TO_BIN('e7f8a9b0-c1d2-3456-ef78-ab90cd123456'), UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), 'India is on the path to becoming a global leader.',  '2022-01-26 08:00:00'),
  (UUID_TO_BIN('f8a9b0c1-d2e3-4567-fa89-bc01de234567'), UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), 'Digital India — empowering every citizen.',          '2022-01-27 09:00:00'),
  -- Ursula von der Leyen (f4d5e6f7)
  (UUID_TO_BIN('a9b0c1d2-e3f4-5678-ab90-cd12ef345678'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), 'The European Union is stronger together.',           '2022-01-01 09:00:00'),
  (UUID_TO_BIN('b0c1d2e3-f4a5-6789-bc01-de23fa456789'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), 'Green Deal: our roadmap to a sustainable future.',   '2022-01-02 10:00:00');

-- ----------------------------------------------------------------
-- Subscriptions
-- ----------------------------------------------------------------
INSERT INTO `subscriptions` (`subscriber_id`, `producer_id`, `created_at`) VALUES
  -- Trudeau follows Macron and Trump
  (UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2020-11-12 14:00:00'),
  (UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), '2020-11-12 14:01:00'),
  -- Merkel follows Macron, Scholz, and von der Leyen
  (UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2020-11-12 09:00:00'),
  (UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), '2021-12-08 12:00:00'),
  (UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), '2021-12-08 12:05:00'),
  -- Macron follows Trudeau and von der Leyen
  (UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), '2020-11-12 14:10:00'),
  (UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), '2022-01-01 10:00:00'),
  -- Trump follows Putin and Modi
  (UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), '2020-11-12 14:30:00'),
  (UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), '2020-11-13 10:00:00'),
  -- Putin follows Trump and Xi
  (UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), UUID_TO_BIN('6e27ea06-a716-4c89-af88-813749a8bd48'), '2020-11-12 17:30:00'),
  (UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), '2020-11-13 08:30:00'),
  -- Scholz follows Merkel, Macron, and von der Leyen
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), UUID_TO_BIN('0dd03a59-7dbc-4d00-8107-3271b3345434'), '2021-12-08 10:00:00'),
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2021-12-08 10:05:00'),
  (UUID_TO_BIN('c1a2b3c4-d5e6-7f89-ab01-cd23ef456789'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), '2021-12-08 10:10:00'),
  -- Xi follows Modi and Putin
  (UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), UUID_TO_BIN('b6f7a8b9-c0d1-2e34-fa56-bc78de901234'), '2020-11-13 09:00:00'),
  (UUID_TO_BIN('c7a8b9c0-d1e2-3f45-ab67-cd89ef012345'), UUID_TO_BIN('abb04b9f-5d10-40dd-9076-eb27ca76891a'), '2020-11-13 09:05:00'),
  -- Meloni follows von der Leyen and Macron
  (UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), '2022-10-23 08:00:00'),
  (UUID_TO_BIN('d2b3c4d5-e6f7-8a90-bc12-de34fa567890'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2022-10-23 08:05:00'),
  -- Sunak follows Trudeau, Macron, and von der Leyen
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), UUID_TO_BIN('70a64b54-43c3-4c18-bbec-64590ff7e0cc'), '2022-10-25 10:00:00'),
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), UUID_TO_BIN('1cd89e11-602a-4186-afbf-e0149b59eb08'), '2022-10-25 10:05:00'),
  (UUID_TO_BIN('e3c4d5e6-f7a8-9b01-cd23-ef45ab678901'), UUID_TO_BIN('f4d5e6f7-a8b9-0c12-de34-fa56bc789012'), '2022-10-25 10:10:00');