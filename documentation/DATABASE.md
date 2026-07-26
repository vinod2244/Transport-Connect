# Database Documentation

## Overview

AP Transport Connect uses MySQL 8.0+ for data persistence. The database is designed following normalization principles with proper indexing for optimal performance.

## Database Schema

### Core Tables

#### 1. Roles & Permissions
- `roles`: Define user roles (admin, customer, driver, owner)
- `permissions`: Define granular permissions
- `role_permissions`: Map permissions to roles

#### 2. Users
- `users`: Core user table with authentication
- `user_devices`: Track user devices and FCM tokens

#### 3. Drivers
- `drivers`: Driver profile with status and ratings
- `driver_documents`: Store driver documents (license, Aadhaar, etc.)

#### 4. Vehicle Owners
- `vehicle_owners`: Owner profile and company info
- `vehicles`: Vehicle listing with documents
- `vehicle_images`: Vehicle photos
- `vehicle_documents`: RC, insurance, fitness, permits

#### 5. Bookings
- `bookings`: Main booking records
- `booking_status_history`: Track booking state changes

#### 6. Payments
- `payments`: Payment transactions
- `wallets`: User wallet balances
- `wallet_transactions`: Wallet activity log
- `withdrawals`: Driver earnings withdrawals

#### 7. Tracking
- `trip_tracking`: GPS coordinates during trips

#### 8. Communication
- `chat_conversations`: Chat threads
- `chat_messages`: Individual messages

#### 9. Ratings & Reviews
- `ratings`: User ratings for drivers, vehicles, etc.

#### 10. Notifications
- `notifications`: Push notifications
- `notification_preferences`: User notification settings

#### 11. Support
- `support_tickets`: Customer support tickets
- `ticket_replies`: Ticket responses

#### 12. Locations
- `countries`: Country data
- `states`: State/Province data
- `districts`: District data
- `cities`: City data with service availability

#### 13. Settings & CMS
- `settings`: Application configuration
- `pages`: CMS pages (Terms, Privacy, etc.)
- `faq`: Frequently asked questions

#### 14. Audit & Logs
- `audit_logs`: Track data changes
- `activity_logs`: Track user activities

#### 15. Promotions
- `promotions`: Discount codes and offers

## Key Design Decisions

### Indexing Strategy
- Primary keys on all tables
- Indexes on frequently queried columns (status, user_id, created_at)
- Composite indexes for common query patterns
- Indexes on foreign keys for join performance

### Data Types
- DECIMAL(12,2) for monetary amounts
- DECIMAL(10,8) for latitude
- DECIMAL(11,8) for longitude
- ENUM for status fields
- TIMESTAMP for automatic date tracking

### Relationships
- Foreign key constraints for data integrity
- CASCADE DELETE where appropriate
- SET NULL for optional relationships
- ON DELETE RESTRICT for critical references

## Performance Considerations

### Connection Pooling
- Configure min_connections: 5
- Configure max_connections: 20
- Idle timeout: 300 seconds

### Query Optimization
- Use EXPLAIN to analyze queries
- Monitor slow query log
- Regular index maintenance
- Partition large tables if needed

### Backup Strategy
- Daily full backups
- Hourly incremental backups
- Keep backups for 30 days
- Test restore procedures

## Maintenance

### Regular Tasks
```bash
# Optimize tables
OPTIMIZE TABLE users, bookings, payments;

# Check table integrity
CHECK TABLE users, bookings, vehicles;

# Analyze table statistics
ANALYZE TABLE users, bookings, drivers;

# Backup database
mysqldump -u user -p database_name > backup.sql
```

## Growth Projections

### Storage Estimates (per year)
- Users: ~1GB (assuming 1M users)
- Bookings: ~5GB (assuming 10M bookings)
- Chat messages: ~10GB
- Tracking data: ~20GB (30-day retention)

### Scaling Strategy
- Table partitioning by date for tracking
- Read replicas for analytics
- Archive old data
- Separate read/write database instances

## Security

- All sensitive data encrypted
- SQL injection prevention via prepared statements
- User data isolation by role
- Audit logging of all changes
- Secure password hashing (bcrypt)
