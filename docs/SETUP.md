# AP Transport Connect - Setup Guide

## 📋 Prerequisites

### System Requirements

#### Backend (PHP)
- PHP 8.3+
- MySQL 8.0+
- Redis 6.0+
- Composer 2.0+
- Node.js 18+ (for admin panel)

#### Android
- Android Studio 2023.1+
- Android SDK 24+ (Min), 34+ (Target)
- JDK 17+
- Kotlin 1.9.10+
- Gradle 8.2.0+

#### Development Machine
- Git
- Docker (Optional, for containerization)
- 10GB+ free disk space
- 8GB+ RAM

---

## 🚀 Backend Setup

### 1. Clone Repository

```bash
git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect/backend
```

### 2. Install Dependencies

```bash
composer install
```

### 3. Environment Configuration

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
APP_NAME="AP Transport Connect"
APP_DEBUG=false
DB_HOST=localhost
DB_DATABASE=ap_transport_connect
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

### 4. Generate Application Key

```bash
php artisan key:generate
```

### 5. Database Setup

```bash
# Run migrations
php artisan migrate

# Seed default data
php artisan db:seed
```

### 6. Generate JWT Keys (if using JWT)

```bash
php artisan jwt:secret
```

### 7. Storage Link

```bash
php artisan storage:link
```

### 8. Cache Configuration

```bash
php artisan config:cache
php artisan route:cache
```

### 9. Start Development Server

```bash
php artisan serve
```

API will be available at: `http://localhost:8000/api/v1`

---

## 📱 Android Setup

### 1. Clone Repository

```bash
git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect/android
```

### 2. Configure Local Properties

Create/Edit `local.properties`:

```properties
sdk.dir=/path/to/android/sdk
ndk.dir=/path/to/android/ndk
```

### 3. API Configuration

Create `app/src/main/res/values/config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="api_base_url">https://api.aptransportconnect.com/api/v1/</string>
    <string name="google_maps_api_key">YOUR_GOOGLE_MAPS_API_KEY</string>
    <string name="firebase_database_url">YOUR_FIREBASE_URL</string>
</resources>
```

### 4. Firebase Configuration

Download `google-services.json` from Firebase Console:

```bash
cp google-services.json app/
```

### 5. Build Android App

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build and run on connected device
./gradlew installDebug
```

### 6. Open in Android Studio

```bash
# Open Android Studio
studio .
```

Then:
- Wait for Gradle sync to complete
- Select `Run → Run 'app'` or press Shift + F10

---

## 🌐 Admin Panel Setup

### 1. Navigate to Admin Directory

```bash
cd Transport-Connect/admin
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Environment Configuration

Create `.env.local`:

```env
VITE_API_BASE_URL=https://api.aptransportconnect.com/api/v1
VITE_APP_NAME="AP Transport Connect Admin"
```

### 4. Development Server

```bash
npm run dev
```

Admin panel will be available at: `http://localhost:5173`

### 5. Build for Production

```bash
npm run build
```

---

## 🗄️ Database Setup

### 1. Create Database

```bash
mysql -u root -p
CREATE DATABASE ap_transport_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 2. Import Schema

```bash
mysql -u root -p ap_transport_connect < database/schemas/complete_schema.sql
```

### 3. Run Migrations

```bash
php artisan migrate
```

### 4. Seed Data

```bash
php artisan db:seed --class=RoleSeeder
php artisan db:seed --class=AdminSeeder
php artisan db:seed --class=VehicleTypeSeeder
```

---

## 🔑 API Keys & Credentials

### Google Maps API

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project
3. Enable Maps JavaScript API, Distance Matrix API, Places API
4. Create API key
5. Add to `.env`

### Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create new project
3. Enable Authentication, Cloud Messaging, Firestore
4. Download service account key
5. Configure in `.env`

### Razorpay Integration

1. Create [Razorpay](https://razorpay.com/) account
2. Get API keys from dashboard
3. Add to `.env`

### Twilio SMS

1. Create [Twilio](https://www.twilio.com/) account
2. Get Account SID and Auth Token
3. Get phone number
4. Add to `.env`

---

## 🐳 Docker Setup (Optional)

### Create docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ap_transport_connect
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  php:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    environment:
      - APP_ENV=production
      - DB_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    volumes:
      - ./backend:/var/www/html

volumes:
  mysql_data:
  redis_data:
```

Run:

```bash
docker-compose up -d
```

---

## ✅ Verification

### Check Backend

```bash
curl -X GET http://localhost:8000/api/v1/health
```

Expected response:
```json
{
  "success": true,
  "message": "API is running"
}
```

### Test Login

```bash
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password"
  }'
```

---

## 🆘 Troubleshooting

### Backend Issues

**Error: No application encryption key**
```bash
php artisan key:generate
```

**Error: Class not found**
```bash
composer dump-autoload
php artisan clear-cache
```

**Database connection refused**
- Check MySQL is running: `mysql -u root -p`
- Verify `.env` database credentials

### Android Issues

**Gradle build fails**
```bash
./gradlew clean
./gradlew build
```

**Device not detected**
```bash
adb devices
adb kill-server
adb start-server
```

### Common Solutions

```bash
# Clear all caches
php artisan cache:clear
php artisan config:clear
php artisan view:clear

# Restart services
sudo systemctl restart mysql
sudo systemctl restart redis-server

# Update packages
composer update
npm update
```

---

## 📚 Documentation Links

- [API Documentation](./API.md)
- [Architecture Guide](./ARCHITECTURE.md)
- [Database Schema](./DATABASE.md)
- [Coding Standards](./CODING_STANDARDS.md)
- [Deployment Guide](./DEPLOYMENT.md)

---

**Last Updated**: 2024
