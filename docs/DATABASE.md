# AP Transport Connect - Database Architecture

## 📊 Database Overview

- **DBMS**: MySQL 8.0+
- **Encoding**: UTF-8 MB4
- **Engine**: InnoDB (ACID compliance)
- **Design Pattern**: Normalized (3NF)

## 🗂️ Database Schema

### 1. Core Tables

#### **Users Table**
Primary table for all user types (Admin, Customer, Driver, Vehicle Owner)

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_photo_url VARCHAR(500),
    
    -- Account Status
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_reason VARCHAR(255),
    
    -- Profile Info
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    
    -- Document Details
    id_document_type ENUM('Aadhar', 'PAN', 'License', 'Passport'),
    id_document_number VARCHAR(100),
    id_document_verified BOOLEAN DEFAULT FALSE,
    
    -- Device & Preferences
    default_language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Indexes
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_role_id (role_id),
    INDEX idx_is_active (is_active),
    CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Roles Table**
Define user roles and permissions

```sql
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    permissions JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Data
INSERT INTO roles (name, description, permissions) VALUES
('admin', 'Administrator', '["manage_users","manage_bookings","manage_payments","view_reports"]'),
('customer', 'Customer/Passenger', '["create_booking","view_booking","cancel_booking","rate_driver"]'),
('driver', 'Driver', '["accept_booking","update_location","complete_booking","view_earnings"]'),
('vehicle_owner', 'Vehicle Owner', '["manage_vehicles","view_analytics","manage_drivers"]');
```

#### **Customers Table**
Extended customer profile information

```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    preferred_payment_method ENUM('Credit Card', 'Debit Card', 'UPI', 'Wallet') DEFAULT 'Credit Card',
    average_rating DECIMAL(3,2) DEFAULT 0,
    total_rides INT DEFAULT 0,
    wallet_balance DECIMAL(10,2) DEFAULT 0,
    referral_code VARCHAR(20),
    referred_by BIGINT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_referred_by FOREIGN KEY (referred_by) REFERENCES customers(id),
    INDEX idx_user_id (user_id),
    INDEX idx_referral_code (referral_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Drivers Table**
Driver-specific information

```sql
CREATE TABLE drivers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    license_expiry_date DATE NOT NULL,
    license_verified BOOLEAN DEFAULT FALSE,
    
    bank_account_number VARCHAR(100),
    bank_name VARCHAR(100),
    bank_account_verified BOOLEAN DEFAULT FALSE,
    
    is_available BOOLEAN DEFAULT TRUE,
    total_rides INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0,
    total_earnings DECIMAL(10,2) DEFAULT 0,
    
    vehicle_owner_id BIGINT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_driver_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_owner_id FOREIGN KEY (vehicle_owner_id) REFERENCES vehicle_owners(id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_available (is_available),
    INDEX idx_license_number (license_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Vehicle Owners Table**
Vehicle owner profile

```sql
CREATE TABLE vehicle_owners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    business_name VARCHAR(200),
    registration_number VARCHAR(100),
    gst_number VARCHAR(20),
    
    bank_account_number VARCHAR(100),
    bank_name VARCHAR(100),
    bank_account_verified BOOLEAN DEFAULT FALSE,
    
    total_vehicles INT DEFAULT 0,
    total_earnings DECIMAL(10,2) DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_owner_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_gst_number (gst_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 2. Vehicle Management Tables

#### **Vehicles Table**
Vehicle information

```sql
CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    
    vehicle_type ENUM('Car', 'Truck', 'Bus', 'Tempo', 'Auto') NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    year INT,
    color VARCHAR(50),
    
    capacity INT NOT NULL,
    seating_capacity INT,
    cargo_capacity_kg INT,
    
    engine_capacity VARCHAR(50),
    fuel_type ENUM('Petrol', 'Diesel', 'CNG', 'Electric') NOT NULL,
    transmission ENUM('Manual', 'Automatic') DEFAULT 'Manual',
    
    registration_document_url VARCHAR(500),
    insurance_document_url VARCHAR(500),
    pollution_certificate_url VARCHAR(500),
    
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    
    mileage_km INT DEFAULT 0,
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES vehicle_owners(id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_registration_number (registration_number),
    INDEX idx_vehicle_type (vehicle_type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Vehicle Assignments Table**
Link drivers to vehicles

```sql
CREATE TABLE vehicle_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    assigned_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_to TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_assignment_driver_id FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_assignment_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    UNIQUE KEY unique_active_assignment (driver_id, vehicle_id, is_active),
    INDEX idx_driver_id (driver_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 3. Booking Management Tables

#### **Bookings Table**
Main booking information

```sql
CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    driver_id BIGINT,
    vehicle_id BIGINT,
    
    -- Location Details
    pickup_location VARCHAR(500) NOT NULL,
    pickup_latitude DECIMAL(10,8),
    pickup_longitude DECIMAL(11,8),
    
    dropoff_location VARCHAR(500) NOT NULL,
    dropoff_latitude DECIMAL(10,8),
    dropoff_longitude DECIMAL(11,8),
    
    -- Journey Details
    booking_type ENUM('One-Way', 'Round-Trip', 'Hourly', 'Contract') NOT NULL,
    scheduled_pickup_time TIMESTAMP,
    estimated_duration_minutes INT,
    estimated_distance_km DECIMAL(10,2),
    
    -- Status
    status ENUM('Requested', 'Accepted', 'Arrived', 'In-Progress', 'Completed', 'Cancelled') DEFAULT 'Requested',
    cancellation_reason VARCHAR(255),
    cancelled_by ENUM('Customer', 'Driver', 'Admin'),
    
    -- Pricing
    base_fare DECIMAL(10,2),
    distance_charge DECIMAL(10,2),
    time_charge DECIMAL(10,2),
    surge_multiplier DECIMAL(3,2) DEFAULT 1.00,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    discount_code VARCHAR(50),
    tax_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    
    -- Timing
    actual_pickup_time TIMESTAMP,
    actual_dropoff_time TIMESTAMP,
    
    special_requests TEXT,
    is_scheduled BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    CONSTRAINT fk_booking_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_booking_driver_id FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_booking_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_status (status),
    INDEX idx_booking_number (booking_number),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Booking Route Stops Table**
For multi-stop bookings

```sql
CREATE TABLE booking_route_stops (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    stop_order INT NOT NULL,
    
    stop_location VARCHAR(500) NOT NULL,
    stop_latitude DECIMAL(10,8),
    stop_longitude DECIMAL(11,8),
    
    arrival_time TIMESTAMP,
    departure_time TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_booking_route_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_booking_id (booking_id),
    INDEX idx_stop_order (stop_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 4. Payment Tables

#### **Payments Table**
Payment records

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_reference_id VARCHAR(100) UNIQUE NOT NULL,
    booking_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Credit Card', 'Debit Card', 'UPI', 'Wallet', 'Cash') NOT NULL,
    
    status ENUM('Pending', 'Processing', 'Completed', 'Failed', 'Refunded') DEFAULT 'Pending',
    payment_gateway ENUM('Razorpay', 'Stripe', 'PayPal') ,
    transaction_id VARCHAR(100),
    
    -- Card Details (if applicable)
    card_last_four VARCHAR(4),
    card_brand VARCHAR(50),
    
    -- Cash Payment
    cash_received_amount DECIMAL(10,2),
    change_returned DECIMAL(10,2),
    
    payment_date TIMESTAMP,
    
    failure_reason VARCHAR(255),
    failure_code VARCHAR(50),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payment_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_payment_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_booking_id (booking_id),
    INDEX idx_status (status),
    INDEX idx_payment_date (payment_date),
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### **Refunds Table**
Refund tracking

```sql
CREATE TABLE refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    
    refund_reason ENUM('Customer Request', 'Driver Cancellation', 'Service Issue', 'Other') NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL,
    
    status ENUM('Pending', 'Processing', 'Completed', 'Failed') DEFAULT 'Pending',
    gateway_refund_id VARCHAR(100),
    
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_refund_payment_id FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_refund_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_payment_id (payment_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 5. Rating & Review Tables

#### **Ratings Table**
Ratings for users and service

```sql
CREATE TABLE ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL UNIQUE,
    
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    from_user_type ENUM('Customer', 'Driver') NOT NULL,
    
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    
    -- Rating Categories
    cleanliness_rating INT CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
    behavior_rating INT CHECK (behavior_rating >= 1 AND behavior_rating <= 5),
    safety_rating INT CHECK (safety_rating >= 1 AND safety_rating <= 5),
    
    helpful_count INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_rating_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_rating_from_user_id FOREIGN KEY (from_user_id) REFERENCES users(id),
    CONSTRAINT fk_rating_to_user_id FOREIGN KEY (to_user_id) REFERENCES users(id),
    INDEX idx_booking_id (booking_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 6. Location & Tracking Tables

#### **Location History Table**
Track driver location during rides

```sql
CREATE TABLE location_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    
    accuracy_meter INT,
    speed_kmh DECIMAL(5,2),
    heading INT,
    
    altitude DECIMAL(10,2),
    
    recorded_at TIMESTAMP NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_location_history_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_location_history_driver_id FOREIGN KEY (driver_id) REFERENCES drivers(id),
    INDEX idx_booking_id (booking_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_recorded_at (recorded_at),
    SPATIAL INDEX sp_location (POINT(latitude, longitude))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 7. Notification Tables

#### **Notifications Table**
User notifications

```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('Booking', 'Payment', 'Promotion', 'System', 'Alert') NOT NULL,
    
    related_booking_id BIGINT,
    related_payment_id BIGINT,
    
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    
    action_url VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_notification_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_booking_id FOREIGN KEY (related_booking_id) REFERENCES bookings(id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 8. Support & Complaint Tables

#### **Support Tickets Table**
Customer support

```sql
CREATE TABLE support_tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    booking_id BIGINT,
    
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('Technical', 'Billing', 'Safety', 'Lost Item', 'Driver Behavior', 'Other') NOT NULL,
    priority ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'Medium',
    
    status ENUM('Open', 'In Progress', 'Waiting for Customer', 'Resolved', 'Closed') DEFAULT 'Open',
    
    assigned_admin_id BIGINT,
    resolution_notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    
    CONSTRAINT fk_ticket_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ticket_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_ticket_admin_id FOREIGN KEY (assigned_admin_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_ticket_number (ticket_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 9. Promotion Tables

#### **Discount Codes Table**
Promotional codes

```sql
CREATE TABLE discount_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    
    discount_type ENUM('Percentage', 'Fixed Amount') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    max_discount_amount DECIMAL(10,2),
    
    min_booking_amount DECIMAL(10,2),
    max_uses INT,
    current_uses INT DEFAULT 0,
    max_uses_per_user INT DEFAULT 1,
    
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    
    applicable_to ENUM('All Users', 'New Users', 'Specific Users') DEFAULT 'All Users',
    
    is_active BOOLEAN DEFAULT TRUE,
    
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_is_active (is_active),
    INDEX idx_valid_until (valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 10. Documents & Compliance Tables

#### **User Documents Table**
Store user document verification

```sql
CREATE TABLE user_documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    
    document_type ENUM('License', 'Aadhar', 'PAN', 'Passport', 'Insurance', 'Registration') NOT NULL,
    document_number VARCHAR(100),
    
    document_url VARCHAR(500) NOT NULL,
    document_expiry_date DATE,
    
    verification_status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    verified_by BIGINT,
    verified_at TIMESTAMP,
    rejection_reason VARCHAR(255),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_doc_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_verified_by FOREIGN KEY (verified_by) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_document_type (document_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 📈 Database Relationships

```
Users (1) ──── (N) Customers
           ──── (N) Drivers
           ──── (N) Vehicle Owners
           ──── (N) Ratings (as from_user_id, to_user_id)

Vehicle Owners (1) ──── (N) Vehicles
Vehicles (1) ──── (N) Vehicle Assignments
Drivers (1) ──── (N) Vehicle Assignments

Customers (1) ──── (N) Bookings
Drivers (1) ──── (N) Bookings
Vehicles (1) ──── (N) Bookings

Bookings (1) ──── (N) Payments
Bookings (1) ──── (N) Ratings
Bookings (1) ──── (N) Location History
Bookings (1) ──── (N) Booking Route Stops

Payments (1) ──── (N) Refunds

Users (1) ──── (N) Notifications
Users (1) ──── (N) Support Tickets
```

---

## 🔑 Indexes Strategy

### Performance Critical Indexes
```sql
-- Frequently filtered columns
INDEX idx_status (status)
INDEX idx_is_active (is_active)
INDEX idx_created_at (created_at)

-- Foreign key lookups
INDEX idx_user_id (user_id)
INDEX idx_booking_id (booking_id)
INDEX idx_driver_id (driver_id)

-- Search operations
INDEX idx_email (email)
INDEX idx_phone (phone)
INDEX idx_booking_number (booking_number)

-- Range queries
SPATIAL INDEX sp_location (POINT(latitude, longitude))
```

---

## 🔐 Data Security

### Encryption
- Passwords: bcrypt hashing
- Sensitive data: AES-256 encryption
- PII: Encrypted at rest

### Access Control
- Role-based access control (RBAC)
- User-level row security
- Admin audit trail

### Backup Strategy
- Daily full backups
- Hourly incremental backups
- Point-in-time recovery enabled
- Replicated to secondary location

---

## 📊 Data Retention Policy

```
Bookings: 2 years
Payments: 2 years
Location History: 90 days
Notifications: 6 months
Support Tickets: 1 year
User Activity: 1 year
Deleted Records: 30 days (hard delete)
```

---

**Last Updated**: 2024
