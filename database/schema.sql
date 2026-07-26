-- AP Transport Connect - Complete Database Schema
-- MySQL 8.0+
-- Character Set: utf8mb4
-- Collation: utf8mb4_unicode_ci

-- ============================================
-- 1. CORE TABLES
-- ============================================

-- Roles Table
CREATE TABLE IF NOT EXISTS `roles` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL UNIQUE,
  `description` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Permissions Table
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT,
  `resource` VARCHAR(50),
  `action` VARCHAR(50),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_resource_action` (`resource`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Role Permissions
CREATE TABLE IF NOT EXISTS `role_permissions` (
  `role_id` INT UNSIGNED NOT NULL,
  `permission_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Users Table
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) UNIQUE,
  `phone` VARCHAR(20) UNIQUE NOT NULL,
  `password_hash` VARCHAR(255),
  `role_id` INT UNSIGNED NOT NULL,
  `profile_image` VARCHAR(255),
  `date_of_birth` DATE,
  `gender` ENUM('male', 'female', 'other'),
  `address` TEXT,
  `city` VARCHAR(50),
  `state` VARCHAR(50),
  `pincode` VARCHAR(10),
  `country` VARCHAR(50),
  `device_token` TEXT,
  `device_id` VARCHAR(255),
  `is_active` BOOLEAN DEFAULT TRUE,
  `is_verified` BOOLEAN DEFAULT FALSE,
  `is_blocked` BOOLEAN DEFAULT FALSE,
  `last_login` TIMESTAMP NULL,
  `login_attempts` INT DEFAULT 0,
  `locked_until` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE RESTRICT,
  INDEX `idx_phone` (`phone`),
  INDEX `idx_email` (`email`),
  INDEX `idx_role_id` (`role_id`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Devices
CREATE TABLE IF NOT EXISTS `user_devices` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `device_id` VARCHAR(255) NOT NULL,
  `device_name` VARCHAR(100),
  `device_type` ENUM('android', 'ios', 'web') NOT NULL,
  `os_version` VARCHAR(50),
  `app_version` VARCHAR(50),
  `device_token` TEXT,
  `last_active` TIMESTAMP,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `unique_device` (`user_id`, `device_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Auth Sessions
CREATE TABLE IF NOT EXISTS `auth_sessions` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `public_id` VARCHAR(64) NOT NULL UNIQUE,
  `device_id` VARCHAR(255) NOT NULL,
  `device_name` VARCHAR(100),
  `device_type` ENUM('android', 'ios', 'web') NOT NULL DEFAULT 'web',
  `ip_address` VARCHAR(45),
  `user_agent` VARCHAR(500),
  `remember_me` BOOLEAN DEFAULT FALSE,
  `last_activity_at` TIMESTAMP NULL,
  `expires_at` TIMESTAMP NOT NULL,
  `revoked_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_auth_sessions_user_id` (`user_id`),
  INDEX `idx_auth_sessions_device_id` (`device_id`),
  INDEX `idx_auth_sessions_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refresh Tokens
CREATE TABLE IF NOT EXISTS `auth_refresh_tokens` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `session_id` BIGINT UNSIGNED NOT NULL,
  `token_hash` CHAR(64) NOT NULL UNIQUE,
  `remember_me` BOOLEAN DEFAULT FALSE,
  `expires_at` TIMESTAMP NOT NULL,
  `revoked_at` TIMESTAMP NULL,
  `revoked_reason` VARCHAR(100),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`session_id`) REFERENCES `auth_sessions`(`id`) ON DELETE CASCADE,
  INDEX `idx_auth_refresh_tokens_user_id` (`user_id`),
  INDEX `idx_auth_refresh_tokens_session_id` (`session_id`),
  INDEX `idx_auth_refresh_tokens_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OTP Challenges
CREATE TABLE IF NOT EXISTS `auth_otps` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `purpose` ENUM('login', 'password_reset') NOT NULL,
  `channel` ENUM('email', 'phone') NOT NULL,
  `destination` VARCHAR(255) NOT NULL,
  `code_hash` VARCHAR(255) NOT NULL,
  `expires_at` TIMESTAMP NOT NULL,
  `attempt_count` INT DEFAULT 0,
  `consumed_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_auth_otps_user_purpose` (`user_id`, `purpose`),
  INDEX `idx_auth_otps_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Password Reset Tokens
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `token_hash` CHAR(64) NOT NULL UNIQUE,
  `source` VARCHAR(50) NOT NULL,
  `expires_at` TIMESTAMP NOT NULL,
  `consumed_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_password_reset_tokens_user_id` (`user_id`),
  INDEX `idx_password_reset_tokens_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. DRIVER TABLES
-- ============================================

-- Drivers Table
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL UNIQUE,
  `license_number` VARCHAR(50) UNIQUE NOT NULL,
  `license_expiry` DATE NOT NULL,
  `experience_years` INT,
  `rating` DECIMAL(3, 2) DEFAULT 0,
  `total_trips` INT DEFAULT 0,
  `total_earnings` DECIMAL(12, 2) DEFAULT 0,
  `status` ENUM('pending', 'approved', 'rejected', 'suspended') DEFAULT 'pending',
  `is_online` BOOLEAN DEFAULT FALSE,
  `current_latitude` DECIMAL(10, 8),
  `current_longitude` DECIMAL(11, 8),
  `current_vehicle_id` INT UNSIGNED,
  `documents_verified` BOOLEAN DEFAULT FALSE,
  `background_check_status` ENUM('pending', 'verified', 'failed') DEFAULT 'pending',
  `bank_account_verified` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`),
  INDEX `idx_is_online` (`is_online`),
  INDEX `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Driver Documents
CREATE TABLE IF NOT EXISTS `driver_documents` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `driver_id` INT UNSIGNED NOT NULL,
  `document_type` ENUM('driving_license', 'aadhaar', 'pan', 'police_verification', 'address_proof', 'bank_details') NOT NULL,
  `document_number` VARCHAR(100),
  `file_path` VARCHAR(255) NOT NULL,
  `is_verified` BOOLEAN DEFAULT FALSE,
  `verified_by` INT UNSIGNED,
  `expiry_date` DATE,
  `rejection_reason` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`driver_id`) REFERENCES `drivers`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`verified_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_driver_id` (`driver_id`),
  INDEX `idx_is_verified` (`is_verified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. OWNER & VEHICLE TABLES
-- ============================================

-- Vehicle Owners
CREATE TABLE IF NOT EXISTS `vehicle_owners` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL UNIQUE,
  `company_name` VARCHAR(150),
  `business_registration_number` VARCHAR(50),
  `total_vehicles` INT DEFAULT 0,
  `total_revenue` DECIMAL(15, 2) DEFAULT 0,
  `rating` DECIMAL(3, 2) DEFAULT 0,
  `status` ENUM('pending', 'approved', 'rejected', 'suspended') DEFAULT 'pending',
  `documents_verified` BOOLEAN DEFAULT FALSE,
  `bank_account_verified` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Types
CREATE TABLE IF NOT EXISTS `vehicle_types` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `capacity_min` INT,
  `capacity_max` INT,
  `description` TEXT,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicles
CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `owner_id` INT UNSIGNED NOT NULL,
  `vehicle_type_id` INT UNSIGNED NOT NULL,
  `registration_number` VARCHAR(20) UNIQUE NOT NULL,
  `vehicle_name` VARCHAR(100),
  `model` VARCHAR(50),
  `color` VARCHAR(30),
  `year_of_manufacture` YEAR,
  `load_capacity` INT,
  `load_capacity_unit` ENUM('kg', 'ton') DEFAULT 'ton',
  `length` DECIMAL(6, 2),
  `width` DECIMAL(6, 2),
  `height` DECIMAL(6, 2),
  `fuel_type` ENUM('diesel', 'petrol', 'cng', 'electric') DEFAULT 'diesel',
  `insurance_number` VARCHAR(100),
  `insurance_expiry` DATE,
  `permit_number` VARCHAR(100),
  `permit_expiry` DATE,
  `fitness_certificate_number` VARCHAR(100),
  `fitness_certificate_expiry` DATE,
  `pollution_certificate_number` VARCHAR(100),
  `pollution_certificate_expiry` DATE,
  `tax_validity` DATE,
  `status` ENUM('available', 'unavailable', 'maintenance', 'documents_pending', 'rejected') DEFAULT 'available',
  `is_verified` BOOLEAN DEFAULT FALSE,
  `is_active` BOOLEAN DEFAULT TRUE,
  `current_latitude` DECIMAL(10, 8),
  `current_longitude` DECIMAL(11, 8),
  `current_driver_id` INT UNSIGNED,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`owner_id`) REFERENCES `vehicle_owners`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`vehicle_type_id`) REFERENCES `vehicle_types`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`current_driver_id`) REFERENCES `drivers`(`id`) ON DELETE SET NULL,
  INDEX `idx_status` (`status`),
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_registration_number` (`registration_number`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Images
CREATE TABLE IF NOT EXISTS `vehicle_images` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `vehicle_id` INT UNSIGNED NOT NULL,
  `image_path` VARCHAR(255) NOT NULL,
  `image_type` ENUM('front', 'side', 'back', 'interior', 'document') DEFAULT 'front',
  `is_primary` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE CASCADE,
  INDEX `idx_vehicle_id` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vehicle Documents
CREATE TABLE IF NOT EXISTS `vehicle_documents` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `vehicle_id` INT UNSIGNED NOT NULL,
  `document_type` ENUM('rc', 'insurance', 'fitness', 'permit', 'puc', 'tax') NOT NULL,
  `document_number` VARCHAR(100),
  `file_path` VARCHAR(255) NOT NULL,
  `is_verified` BOOLEAN DEFAULT FALSE,
  `verified_by` INT UNSIGNED,
  `expiry_date` DATE NOT NULL,
  `rejection_reason` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`verified_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_vehicle_id` (`vehicle_id`),
  INDEX `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. BOOKING TABLES
-- ============================================

-- Bookings
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `booking_number` VARCHAR(50) UNIQUE NOT NULL,
  `customer_id` INT UNSIGNED NOT NULL,
  `vehicle_id` INT UNSIGNED NOT NULL,
  `driver_id` INT UNSIGNED,
  `owner_id` INT UNSIGNED NOT NULL,
  `pickup_location` VARCHAR(255) NOT NULL,
  `pickup_latitude` DECIMAL(10, 8) NOT NULL,
  `pickup_longitude` DECIMAL(11, 8) NOT NULL,
  `dropoff_location` VARCHAR(255) NOT NULL,
  `dropoff_latitude` DECIMAL(10, 8) NOT NULL,
  `dropoff_longitude` DECIMAL(11, 8) NOT NULL,
  `load_type` VARCHAR(100),
  `load_weight` INT,
  `load_weight_unit` ENUM('kg', 'ton') DEFAULT 'ton',
  `scheduled_pickup_time` DATETIME,
  `estimated_delivery_time` DATETIME,
  `actual_pickup_time` DATETIME,
  `actual_delivery_time` DATETIME,
  `estimated_distance` DECIMAL(10, 2),
  `actual_distance` DECIMAL(10, 2),
  `estimated_fare` DECIMAL(12, 2) NOT NULL,
  `final_fare` DECIMAL(12, 2),
  `discount_amount` DECIMAL(10, 2) DEFAULT 0,
  `tax_amount` DECIMAL(10, 2) DEFAULT 0,
  `total_amount` DECIMAL(12, 2),
  `status` ENUM('pending', 'accepted', 'rejected', 'started', 'completed', 'cancelled') DEFAULT 'pending',
  `cancellation_reason` TEXT,
  `cancelled_by` ENUM('customer', 'driver', 'owner', 'admin'),
  `notes` TEXT,
  `special_instructions` TEXT,
  `is_return_trip` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`customer_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`driver_id`) REFERENCES `drivers`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`owner_id`) REFERENCES `vehicle_owners`(`id`) ON DELETE RESTRICT,
  INDEX `idx_status` (`status`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_driver_id` (`driver_id`),
  INDEX `idx_vehicle_id` (`vehicle_id`),
  INDEX `idx_booking_number` (`booking_number`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Booking Status History
CREATE TABLE IF NOT EXISTS `booking_status_history` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT UNSIGNED NOT NULL,
  `status` ENUM('pending', 'accepted', 'rejected', 'started', 'completed', 'cancelled') NOT NULL,
  `changed_by` INT UNSIGNED,
  `reason` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`changed_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_booking_id` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. PAYMENT TABLES
-- ============================================

-- Payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `transaction_id` VARCHAR(100) UNIQUE,
  `booking_id` INT UNSIGNED,
  `user_id` INT UNSIGNED NOT NULL,
  `amount` DECIMAL(12, 2) NOT NULL,
  `currency` VARCHAR(3) DEFAULT 'INR',
  `payment_method` ENUM('card', 'upi', 'wallet', 'netbanking', 'cod') NOT NULL,
  `payment_gateway` ENUM('razorpay', 'phonepe', 'manual') NOT NULL,
  `gateway_reference_id` VARCHAR(100),
  `status` ENUM('pending', 'initiated', 'processing', 'completed', 'failed', 'refunded') DEFAULT 'pending',
  `refund_status` ENUM('none', 'partial', 'full') DEFAULT 'none',
  `refund_amount` DECIMAL(12, 2) DEFAULT 0,
  `notes` TEXT,
  `failed_reason` TEXT,
  `retry_count` INT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  INDEX `idx_status` (`status`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_booking_id` (`booking_id`),
  INDEX `idx_transaction_id` (`transaction_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Wallets
CREATE TABLE IF NOT EXISTS `wallets` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL UNIQUE,
  `balance` DECIMAL(12, 2) DEFAULT 0,
  `total_added` DECIMAL(15, 2) DEFAULT 0,
  `total_spent` DECIMAL(15, 2) DEFAULT 0,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Wallet Transactions
CREATE TABLE IF NOT EXISTS `wallet_transactions` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `wallet_id` INT UNSIGNED NOT NULL,
  `transaction_type` ENUM('add', 'spend', 'refund', 'bonus', 'admin_credit') NOT NULL,
  `amount` DECIMAL(12, 2) NOT NULL,
  `description` TEXT,
  `reference_id` VARCHAR(100),
  `reference_type` ENUM('booking', 'payment', 'bonus', 'admin') DEFAULT 'admin',
  `previous_balance` DECIMAL(12, 2),
  `new_balance` DECIMAL(12, 2),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`wallet_id`) REFERENCES `wallets`(`id`) ON DELETE CASCADE,
  INDEX `idx_wallet_id` (`wallet_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Withdrawals
CREATE TABLE IF NOT EXISTS `withdrawals` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `amount` DECIMAL(12, 2) NOT NULL,
  `bank_account_id` INT UNSIGNED,
  `status` ENUM('pending', 'approved', 'processing', 'completed', 'failed', 'cancelled') DEFAULT 'pending',
  `transaction_id` VARCHAR(100),
  `failure_reason` TEXT,
  `requested_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `processed_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. TRACKING TABLES
-- ============================================

-- Trip Tracking
CREATE TABLE IF NOT EXISTS `trip_tracking` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT UNSIGNED NOT NULL,
  `latitude` DECIMAL(10, 8) NOT NULL,
  `longitude` DECIMAL(11, 8) NOT NULL,
  `accuracy` INT,
  `speed` DECIMAL(6, 2),
  `direction` INT,
  `altitude` INT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE CASCADE,
  INDEX `idx_booking_id` (`booking_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. COMMUNICATION TABLES
-- ============================================

-- Chat Conversations
CREATE TABLE IF NOT EXISTS `chat_conversations` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT UNSIGNED,
  `participant_1_id` INT UNSIGNED NOT NULL,
  `participant_2_id` INT UNSIGNED NOT NULL,
  `conversation_type` ENUM('user_driver', 'user_owner', 'admin_user') DEFAULT 'user_driver',
  `last_message` TEXT,
  `last_message_at` TIMESTAMP NULL,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`participant_1_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`participant_2_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_participants` (`participant_1_id`, `participant_2_id`),
  INDEX `idx_booking_id` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chat Messages
CREATE TABLE IF NOT EXISTS `chat_messages` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `conversation_id` INT UNSIGNED NOT NULL,
  `sender_id` INT UNSIGNED NOT NULL,
  `message_type` ENUM('text', 'image', 'audio', 'document') DEFAULT 'text',
  `content` LONGTEXT,
  `file_path` VARCHAR(255),
  `file_size` INT,
  `is_read` BOOLEAN DEFAULT FALSE,
  `read_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`conversation_id`) REFERENCES `chat_conversations`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  INDEX `idx_conversation_id` (`conversation_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. RATING & REVIEW TABLES
-- ============================================

-- Ratings
CREATE TABLE IF NOT EXISTS `ratings` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT UNSIGNED NOT NULL UNIQUE,
  `rater_id` INT UNSIGNED NOT NULL,
  `ratee_id` INT UNSIGNED NOT NULL,
  `rating_type` ENUM('driver', 'vehicle', 'owner', 'customer') NOT NULL,
  `rating` DECIMAL(3, 2) NOT NULL,
  `comment` TEXT,
  `categories` JSON,
  `is_anonymous` BOOLEAN DEFAULT FALSE,
  `helpful_count` INT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`rater_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`ratee_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_booking_id` (`booking_id`),
  INDEX `idx_rating_type` (`rating_type`),
  INDEX `idx_ratee_id` (`ratee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 9. NOTIFICATION TABLES
-- ============================================

-- Notifications
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `message` TEXT,
  `notification_type` ENUM('booking', 'payment', 'trip', 'otp', 'document', 'offer', 'general', 'alert') NOT NULL,
  `reference_id` INT UNSIGNED,
  `reference_type` ENUM('booking', 'payment', 'user') DEFAULT 'booking',
  `action_url` VARCHAR(255),
  `is_read` BOOLEAN DEFAULT FALSE,
  `read_at` TIMESTAMP NULL,
  `is_sent` BOOLEAN DEFAULT FALSE,
  `sent_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notification Preferences
CREATE TABLE IF NOT EXISTS `notification_preferences` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL UNIQUE,
  `push_notifications` BOOLEAN DEFAULT TRUE,
  `email_notifications` BOOLEAN DEFAULT TRUE,
  `sms_notifications` BOOLEAN DEFAULT TRUE,
  `booking_notifications` BOOLEAN DEFAULT TRUE,
  `payment_notifications` BOOLEAN DEFAULT TRUE,
  `promotional_notifications` BOOLEAN DEFAULT FALSE,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 10. SUPPORT & COMPLAINT TABLES
-- ============================================

-- Support Tickets
CREATE TABLE IF NOT EXISTS `support_tickets` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `ticket_number` VARCHAR(50) UNIQUE NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `booking_id` INT UNSIGNED,
  `subject` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `category` VARCHAR(50),
  `priority` ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
  `status` ENUM('open', 'in_progress', 'on_hold', 'resolved', 'closed') DEFAULT 'open',
  `assigned_to` INT UNSIGNED,
  `resolution` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `resolved_at` TIMESTAMP NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`booking_id`) REFERENCES `bookings`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`assigned_to`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_status` (`status`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ticket Replies
CREATE TABLE IF NOT EXISTS `ticket_replies` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `ticket_id` INT UNSIGNED NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `reply_text` TEXT NOT NULL,
  `attachment_path` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  INDEX `idx_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 11. AUDIT & LOGGING TABLES
-- ============================================

-- Audit Logs
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED,
  `action` VARCHAR(100) NOT NULL,
  `resource_type` VARCHAR(50) NOT NULL,
  `resource_id` INT UNSIGNED,
  `old_values` JSON,
  `new_values` JSON,
  `ip_address` VARCHAR(45),
  `user_agent` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_action` (`action`),
  INDEX `idx_resource_type` (`resource_type`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Activity Logs
CREATE TABLE IF NOT EXISTS `activity_logs` (
  `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED,
  `activity_type` VARCHAR(50) NOT NULL,
  `description` TEXT,
  `ip_address` VARCHAR(45),
  `user_agent` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_activity_type` (`activity_type`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 12. LOCATION TABLES
-- ============================================

-- Countries
CREATE TABLE IF NOT EXISTS `countries` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `code` VARCHAR(3) UNIQUE,
  `currency` VARCHAR(3),
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- States
CREATE TABLE IF NOT EXISTS `states` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `country_id` INT UNSIGNED NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(3),
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`country_id`) REFERENCES `countries`(`id`) ON DELETE CASCADE,
  INDEX `idx_country_id` (`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Districts
CREATE TABLE IF NOT EXISTS `districts` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `state_id` INT UNSIGNED NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `latitude` DECIMAL(10, 8),
  `longitude` DECIMAL(11, 8),
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`state_id`) REFERENCES `states`(`id`) ON DELETE CASCADE,
  INDEX `idx_state_id` (`state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cities
CREATE TABLE IF NOT EXISTS `cities` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `district_id` INT UNSIGNED NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `latitude` DECIMAL(10, 8),
  `longitude` DECIMAL(11, 8),
  `service_enabled` BOOLEAN DEFAULT TRUE,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`district_id`) REFERENCES `districts`(`id`) ON DELETE CASCADE,
  INDEX `idx_district_id` (`district_id`),
  INDEX `idx_service_enabled` (`service_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 13. SETTINGS & CMS TABLES
-- ============================================

-- Settings
CREATE TABLE IF NOT EXISTS `settings` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `key` VARCHAR(100) NOT NULL UNIQUE,
  `value` LONGTEXT,
  `type` ENUM('string', 'integer', 'boolean', 'json', 'array') DEFAULT 'string',
  `group` VARCHAR(50),
  `description` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_group` (`group`),
  INDEX `idx_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Pages (CMS)
CREATE TABLE IF NOT EXISTS `pages` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `slug` VARCHAR(150) NOT NULL UNIQUE,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `content` LONGTEXT,
  `author_id` INT UNSIGNED,
  `is_published` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FAQ
CREATE TABLE IF NOT EXISTS `faq` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `question` VARCHAR(500) NOT NULL,
  `answer` LONGTEXT NOT NULL,
  `category` VARCHAR(50),
  `sort_order` INT DEFAULT 0,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category`),
  INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 14. PROMOTIONS & OFFERS
-- ============================================

-- Promotions
CREATE TABLE IF NOT EXISTS `promotions` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `code` VARCHAR(50) UNIQUE NOT NULL,
  `description` TEXT,
  `discount_type` ENUM('percentage', 'fixed') NOT NULL,
  `discount_value` DECIMAL(10, 2) NOT NULL,
  `min_booking_amount` DECIMAL(12, 2),
  `max_discount_amount` DECIMAL(12, 2),
  `max_uses` INT,
  `current_uses` INT DEFAULT 0,
  `uses_per_user` INT DEFAULT 1,
  `applicable_to` ENUM('all', 'new_users', 'specific_users') DEFAULT 'all',
  `start_date` DATE,
  `end_date` DATE,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_code` (`code`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 15. INSERT INITIAL DATA
-- ============================================

-- Insert Roles
INSERT INTO `roles` (`name`, `description`) VALUES
('admin', 'Administrator with full access'),
('customer', 'Customer who books transport'),
('driver', 'Driver who operates vehicles'),
('owner', 'Vehicle owner');

-- Insert Vehicle Types
INSERT INTO `vehicle_types` (`name`, `capacity_min`, `capacity_max`, `description`) VALUES
('Mini Truck', 1, 2, 'Small truck for light loads'),
('Pickup', 1, 1.5, 'Open pickup truck'),
('Tata Ace', 1, 1, 'Popular Indian three-wheeler'),
('Container', 5, 20, 'Containerized transport'),
('Trailer', 10, 40, 'Large trailer truck'),
('Tipper', 8, 12, 'Tipper truck for bulk material'),
('Lorry', 12, 20, 'Standard commercial truck'),
('Flatbed', 8, 15, 'Flatbed truck for machinery'),
('Refrigerated Truck', 5, 10, 'Temperature controlled transport'),
('Tanker', 10, 20, 'Liquid transport tanker'),
('Heavy Truck', 15, 40, 'Heavy commercial vehicle'),
('LCV', 1, 3, 'Light commercial vehicle');

-- Insert Country (India)
INSERT INTO `countries` (`name`, `code`, `currency`) VALUES
('India', 'IND', 'INR');

-- Insert Andhra Pradesh State
INSERT INTO `states` (`country_id`, `name`, `code`) VALUES
(1, 'Andhra Pradesh', 'AP');

-- Insert Districts in Andhra Pradesh
INSERT INTO `districts` (`state_id`, `name`) VALUES
(1, 'Visakhapatnam'),
(1, 'Krishna'),
(1, 'Guntur'),
(1, 'Prakasam'),
(1, 'Nellore'),
(1, 'Chittoor'),
(1, 'Kadapa');

-- Insert Cities
INSERT INTO `cities` (`district_id`, `name`, `latitude`, `longitude`, `service_enabled`) VALUES
(1, 'Visakhapatnam', 17.6869, 83.2185, TRUE),
(2, 'Vijayawada', 16.5062, 80.6480, TRUE),
(3, 'Guntur', 16.3067, 80.4365, TRUE),
(4, 'Ongole', 14.6349, 79.9787, TRUE),
(5, 'Nellore', 14.4426, 79.9864, TRUE);

-- Create indexes for better performance
CREATE INDEX idx_payment_gateway ON payments(payment_gateway);
CREATE INDEX idx_booking_date ON bookings(created_at);
CREATE INDEX idx_vehicle_status ON vehicles(status, owner_id);
CREATE INDEX idx_driver_status ON drivers(status, is_online);
CREATE INDEX idx_wallet_balance ON wallets(balance);
CREATE INDEX idx_message_read ON chat_messages(is_read, created_at);

-- Create views for common queries
CREATE VIEW active_vehicles AS
SELECT * FROM vehicles WHERE is_active = TRUE AND status = 'available';

CREATE VIEW online_drivers AS
SELECT * FROM drivers WHERE is_online = TRUE AND status = 'approved';

CREATE VIEW pending_bookings AS
SELECT * FROM bookings WHERE status = 'pending';

CREATE VIEW completed_bookings AS
SELECT * FROM bookings WHERE status = 'completed';
