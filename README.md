# AP Transport Connect - Production Ready Transport Marketplace Platform

![AP Transport Connect](https://img.shields.io/badge/Status-Production--Ready-brightgreen)
![Kotlin](https://img.shields.io/badge/Android-Kotlin-blue)
![PHP](https://img.shields.io/badge/Backend-PHP%208.3-blueviolet)
![MySQL](https://img.shields.io/badge/Database-MySQL%208-orange)

## Overview

**AP Transport Connect** is a comprehensive transport marketplace platform similar to **Uber Freight, Porter, and BlackBuck**, enabling users to book transport vehicles across Andhra Pradesh while allowing vehicle owners and drivers to manage requests through dedicated mobile applications.

## Platform Features

### 🚀 Core Capabilities

- **Multi-Role Authentication**: Admin, Customer, Driver, Vehicle Owner
- **Advanced Booking System**: Real-time vehicle search and booking confirmation
- **Live GPS Tracking**: Real-time tracking with polylines and ETA
- **Payment Integration**: Razorpay, PhonePe, UPI, Wallet, COD
- **Driver-Customer Communication**: Chat, voice notes, calls
- **Document Management**: Vehicle RC, Insurance, Fitness, Permit, PUC, Tax
- **Analytics Dashboard**: Revenue, bookings, vehicle usage, driver performance
- **Firebase Push Notifications**: Booking updates, payments, trip status
- **Role-Based Access Control**: Granular permissions and audit logs

## Technology Stack

### Android (Kotlin)
- MVVM Architecture
- Repository Pattern
- Retrofit + Coroutines
- Room Database
- Hilt Dependency Injection
- Google Maps & Places
- Material Design 3
- FCM Push Notifications
- WorkManager for background tasks

### Backend (PHP)
- PHP 8.3
- MySQL 8.0
- REST APIs with JWT Authentication
- PDO for database operations
- Composer dependency management
- Apache/Nginx compatible

### Admin Panel
- Bootstrap 5 + AdminLTE
- Responsive Design
- Real-time Analytics
- DataTables with sorting/filtering
- Role-based access

## Project Structure

```
Transport-Connect/
├── android/
│   ├── UserApp/                 # Customer Android App
│   ├── DriverApp/               # Driver Android App
│   └── OwnerApp/                # Vehicle Owner Android App
├── backend/
│   ├── app/
│   ├── config/
│   ├── routes/
│   ├── .env.example
│   └── composer.json
├── admin/                       # Admin Panel
├── database/
│   └── schema.sql
├── documentation/
│   ├── API.md
│   ├── AUTHENTICATION.md
│   └── DATABASE.md
├── postman/
│   └── AP_Transport_Connect.postman_collection.json
├── deployment/
│   ├── nginx.conf
│   └── apache.conf
└── README.md
```

## Quick Start

### Prerequisites
- Android Studio (Latest)
- PHP 8.3+
- MySQL 8.0+
- Composer
- Node.js (for admin panel)
- Git

### Installation

1. **Clone Repository**
   ```bash
   git clone https://github.com/vinod2244/Transport-Connect.git
   cd Transport-Connect
   ```

2. **Backend Setup**
   ```bash
   cd backend
   composer install
   cp .env.example .env
   php artisan migrate
   php -S localhost:8000
   ```

3. **Database Setup**
   ```bash
   mysql -u root -p < database/schema.sql
   ```

4. **Android Setup**
   - Open `android/UserApp` in Android Studio
   - Update API base URL in `BuildConfig`
   - Add Firebase Google Services JSON
   - Build and run

## User Roles

### 1. Customer
- Search and book vehicles
- Live tracking
- Chat with driver
- Payment processing
- Rating and reviews

### 2. Driver
- Accept/reject trips
- Live location sharing
- Navigation integration
- Earnings tracking
- Document management

### 3. Vehicle Owner
- Vehicle registration and management
- Driver assignment
- Booking approval
- Revenue tracking
- Document verification

### 4. Admin
- Complete platform management
- User and driver verification
- Payment and settlement
- Analytics and reporting
- Complaint resolution

## API Documentation

Detailed API documentation is available in `documentation/API.md`

### Key Endpoints

**Authentication**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

**Vehicles**
- `GET /api/vehicles/search` - Search vehicles
- `GET /api/vehicles/{id}` - Get vehicle details
- `POST /api/vehicles` - Register vehicle (owner only)

**Bookings**
- `POST /api/bookings` - Create booking
- `GET /api/bookings/{id}` - Get booking details
- `PUT /api/bookings/{id}/status` - Update booking status
- `GET /api/bookings/history` - Booking history

**Tracking**
- `GET /api/tracking/{bookingId}` - Real-time tracking
- `POST /api/tracking/location` - Update location

**Payments**
- `POST /api/payments/initiate` - Initiate payment
- `POST /api/payments/verify` - Verify payment
- `GET /api/payments/history` - Payment history

## Authentication

### Login Methods
- Role-aware login for `customer`, `driver`, `owner`, and `admin`
- Email/phone + password login
- Email/phone OTP login with expiry, retry limits, and resend throttling
- Refresh-token based sessions with rotation and revocation
- Remember Me support via extended refresh-token lifetime

### Security
- JWT access tokens with role + session claims
- Server-side refresh-token storage with hashing, rotation, and revocation
- Session tracking per device with last activity timestamps
- Password reset + change password flows
- RBAC guard enforcement on protected routes
- Password hashing (bcrypt)
- Prepared statements (SQL Injection protection)
- XSS protection
- HTTPS enforcement
- Rate limiting
- Device management

## Database Schema

Key tables:
- `users` - User accounts
- `roles` - User roles
- `permissions` - Role permissions
- `auth_sessions` - Active device/session tracking
- `auth_refresh_tokens` - Refresh token rotation + revocation records
- `auth_otps` - OTP delivery + verification challenges
- `password_reset_tokens` - Forgot-password verification grants
- `vehicles` - Vehicle listings
- `bookings` - Booking records
- `drivers` - Driver profiles
- `owners` - Owner profiles
- `payments` - Payment records
- `chat_messages` - Chat history
- `ratings` - User and vehicle ratings
- `notifications` - Push notifications

Full schema available in `database/schema.sql`

### Auth API Highlights
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/send-otp`
- `POST /api/auth/verify-otp`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/logout-all`
- `GET /api/auth/sessions`
- `POST /api/auth/forgot-password`
- `POST /api/auth/verify-password-reset`
- `POST /api/auth/reset-password`
- `POST /api/auth/change-password`

## Notifications

### Firebase Push Notifications
- Trip acceptance
- Booking confirmation
- Trip completion
- Payment notifications
- Document expiry reminders
- Promotional offers

### Email Notifications
- Welcome email
- OTP verification
- Booking confirmation
- Invoice receipt
- Payment receipt

## Payment Integration

### Supported Methods
- **Razorpay**: Credit/Debit card, UPI, Wallet
- **PhonePe**: UPI, Wallet
- **Manual**: Cash on Delivery (COD)
- **Wallet**: In-app wallet balance

## Analytics & Reporting

### Admin Dashboard
- Real-time revenue tracking
- Active drivers and vehicles
- Booking trends
- Vehicle utilization
- Driver performance metrics
- Customer growth analytics
- Heat maps of popular routes

## Security Features

- ✅ JWT Authentication with refresh tokens
- ✅ Role-Based Access Control (RBAC)
- ✅ Prepared statements for SQL injection prevention
- ✅ Password hashing with bcrypt
- ✅ XSS protection
- ✅ HTTPS enforcement
- ✅ Rate limiting
- ✅ Audit logging
- ✅ Device management
- ✅ Biometric security

## Deployment

### Server Requirements
- PHP 8.3+
- MySQL 8.0+
- Apache/Nginx
- SSL Certificate
- 2GB RAM minimum
- 10GB Storage

### Deployment Guide
See `deployment/` directory for:
- Nginx configuration
- Apache configuration
- Database backup/restore scripts
- Environment setup

## Documentation

- [API Documentation](./documentation/API.md)
- [Authentication Guide](./documentation/AUTHENTICATION.md)
- [Database Schema](./documentation/DATABASE.md)
- [Deployment Guide](./deployment/DEPLOYMENT.md)
- [Architecture Guide](./documentation/ARCHITECTURE.md)

## Postman Collection

Import `postman/AP_Transport_Connect.postman_collection.json` in Postman for API testing.

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## License

This project is proprietary and confidential.

## Support

For support, email: support@aptransportconnect.com

## Author

**Vinod Kumar**
- GitHub: [@vinod2244](https://github.com/vinod2244)

---

**Last Updated**: July 26, 2024
**Version**: 1.0.0

## Android Driver App (`android/driver`)

### Build & Run
1. Open the `android/` folder in Android Studio.
2. Add `google-services.json` to `android/driver/` if you want live Firebase services.
3. Build the driver app with the `driver` run configuration or `gradle :driver:assembleDebug`.
4. Install the generated APK for package `com.aptransportconnect.driver`.

### Google Maps Setup
- Replace the `GOOGLE_MAPS_API_KEY` manifest placeholder in `android/driver/build.gradle.kts`.
- Enable Maps SDK for Android in your Google Cloud project.
- Restrict the key to the driver app package and SHA-1 certificate.

### Firebase / FCM Setup
- Create a Firebase Android app for `com.aptransportconnect.driver`.
- Download `google-services.json` and place it in `android/driver/`.
- Enable Firebase Cloud Messaging and use `DriverFirebaseMessagingService` for token sync and notification handling.

### Driver App Architecture
- **UI**: Jetpack Compose + Material 3 screens with Compose Navigation.
- **Presentation**: MVVM view models using `StateFlow` and coroutines.
- **Domain**: Repository interfaces and focused use cases for trips, wallet, earnings, and location.
- **Data**: Retrofit/OkHttp networking, Room offline cache, and DataStore-backed JWT/session preferences.
- **Services**: Foreground GPS tracking service and Firebase messaging service.
