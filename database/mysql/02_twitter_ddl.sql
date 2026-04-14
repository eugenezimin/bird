-- ================================================================
-- FILE: 02_twitter_ddl.sql
-- Usage: mysql -u root -p < 02_twitter_ddl.sql
-- ================================================================
drop SCHEMA if EXISTS `twitter`;

CREATE DATABASE IF NOT EXISTS `twitter`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `twitter`;

-- ----------------------------------------------------------------
-- Messages
-- author_id is a logical FK to ums.users.id — enforced in the
-- application layer via UMSConnector (cross-DB FKs are not
-- supported by MySQL).
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `messages` (
  `id`         BINARY(16)   NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `author_id`  BINARY(16)   NOT NULL,
  `content`    VARCHAR(280) NOT NULL,
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_messages_author` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- Subscriptions
-- Both columns are logical FKs to ums.users.id.
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscriptions` (
  `subscriber_id` BINARY(16) NOT NULL,
  `producer_id`   BINARY(16) NOT NULL,
  `created_at`    DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subscriber_id`, `producer_id`),
  KEY `idx_subs_producer` (`producer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;