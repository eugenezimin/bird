-- ================================================================
-- FILE: 01_ums_ddl.sql
-- Usage: mysql -u root -p < 01_ums_ddl.sql
-- ================================================================
DROP SCHEMA IF EXISTS `ums`;

CREATE DATABASE IF NOT EXISTS `ums`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `ums`;

-- ----------------------------------------------------------------
-- Roles lookup
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
  `id`          BINARY(16)   NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `name`        VARCHAR(50)  NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_roles_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- Users
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id`            BINARY(16)   NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `name`          VARCHAR(100) NOT NULL,
  `email`         VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- Users <-> Roles (many-to-many)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users_roles` (
  `user_id` BINARY(16) NOT NULL,
  `role_id` BINARY(16) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------
-- Sessions
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sessions` (
  `id`            BINARY(16) NOT NULL DEFAULT (UUID_TO_BIN(UUID())),
  `user_id`       BINARY(16) NOT NULL,
  `logged_in_at`  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `logged_out_at` DATETIME   DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sessions_user` (`user_id`),
  CONSTRAINT `fk_sessions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;