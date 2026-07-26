# Development Environment Setup

## Prerequisites

### System Requirements
- macOS 10.15+ / Windows 10+ / Ubuntu 18.04+
- Git
- 8GB RAM minimum
- 50GB free disk space

### Tools Installation

#### 1. Android Development

**Android Studio**
```bash
# macOS (using Homebrew)
brew install android-studio

# Or download from: https://developer.android.com/studio
```

**Android SDK**
- API Level: 28+ (minimum)
- Target API Level: 34+
- Android 14 (API 34)
- Build Tools: 34.0.0

**Virtual Device Setup**
- Create AVD with API 34
- Resolution: 1080x2340 (typical mobile)
- RAM: 2GB
- Storage: 100MB internal

#### 2. PHP Development

**PHP 8.3**
```bash
# macOS
brew install php@8.3
brew install composer

# Ubuntu
sudo apt-get install php8.3 php8.3-mysql php8.3-json php8.3-curl composer

# Windows
# Download from https://www.php.net/downloads
# Or use XAMPP/WAMP
```

**Composer**
```bash
# Install globally
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer
```

#### 3. MySQL Database

```bash
# macOS
brew install mysql@8.0
brew services start mysql@8.0

# Ubuntu
sudo apt-get install mysql-server-8.0
sudo systemctl start mysql

# Windows
# Download MySQL Installer from https://dev.mysql.com/downloads/mysql/
```

**MySQL Configuration**
```bash
# Secure MySQL installation
mysql_secure_installation

# Create database user
mysql -u root -p

CREATE USER 'transport_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON transport_connect.* TO 'transport_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 4. Node.js (Admin Panel)

```bash
# macOS
brew install node@18

# Ubuntu
curl -sL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install nodejs

# Verify
node -v  # Should be v18+
npm -v   # Should be v9+
```

#### 5. Docker (Optional)

```bash
# macOS & Windows
# Download from https://www.docker.com/products/docker-desktop

# Ubuntu
sudo apt-get install docker.io docker-compose
sudo usermod -aG docker $USER
```

## Project Setup

### 1. Clone Repository

```bash
git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect
```

### 2. Backend Setup

```bash
cd backend

# Install dependencies
composer install

# Create environment file
cp .env.example .env

# Edit .env with your settings
vim .env  # or nano .env

# Generate JWT secret
openssl rand -base64 32  # Copy this to JWT_SECRET in .env

# Create database
mysql -u root -p -e "CREATE DATABASE transport_connect;"

# Import database schema
mysql -u transport_user -p transport_connect < ../database/schema.sql

# Start PHP development server
php -S localhost:8000 -t public/

# In another terminal, test API
curl http://localhost:8000/api/health
```

### 3. Database Setup

```bash
cd database

# Create database
mysql -u root -p
```

```sql
CREATE DATABASE transport_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE transport_connect;
SOURCE schema.sql;
```

### 4. Admin Panel Setup

```bash
cd admin

# Install dependencies
npm install

# Create environment file
cp .env.example .env.local

# Edit .env.local
# REACT_APP_API_URL=http://localhost:8000/api

# Start development server
npm start

# Build for production
npm run build
```

### 5. Android App Setup

**UserApp**
```bash
cd android/UserApp

# Sync Gradle files
# In Android Studio: File > Sync Now

# Update local.properties with SDK path
echo "sdk.dir=/path/to/android/sdk" > local.properties

# Update API configuration
# Edit: app/src/main/java/com/aptransportconnect/user/network/ApiClient.kt
# Set BASE_URL = "http://your-api-url/api"

# Update Firebase config
# Place google-services.json in app/ directory

# Build APK
./gradlew assembleDebug

# Build APK for production
./gradlew assembleRelease

# Run on emulator
./gradlew installDebug
```

**DriverApp & OwnerApp**: Follow same steps

## Environment Configuration

### Backend .env

```env
# App Configuration
APP_NAME=APTransportConnect
APP_ENV=development
APP_DEBUG=true

# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=transport_connect
DB_USER=transport_user
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_jwt_secret_here
JWT_EXPIRY=86400  # 24 hours
JWT_REFRESH_EXPIRY=604800  # 7 days

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_API_KEY=your-api-key
FIREBASE_SERVICE_ACCOUNT_JSON=/path/to/service-account.json

# Razorpay Payment
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret

# Email Configuration
MAIL_DRIVER=smtp
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_ENCRYPTION=tls
MAIL_FROM_ADDRESS=noreply@aptransportconnect.com
MAIL_FROM_NAME=AP Transport Connect

# SMS Configuration (Twilio)
SMS_DRIVER=twilio
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890

# API Rate Limiting
RATE_LIMIT_REQUESTS=100
RATE_LIMIT_PERIOD=60  # seconds

# File Upload
MAX_UPLOAD_SIZE=10485760  # 10MB in bytes
ALLOWED_FILE_TYPES=jpg,jpeg,png,pdf,doc,docx

# Google Maps
GOOGLE_MAPS_API_KEY=your_api_key
GOOGLE_PLACES_API_KEY=your_api_key

# Server
SERVER_HOST=localhost
SERVER_PORT=8000
```

### Android Build Gradle

```gradle
buildFeatures {
    buildConfig true
}

buildTypes {
    debug {
        buildConfigField "String", "API_BASE_URL", '"http://localhost:8000/api/"'
        debuggable true
    }
    release {
        buildConfigField "String", "API_BASE_URL", '"https://api.aptransportconnect.com/api/"'
        debuggable false
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

## IDE Configuration

### Android Studio

1. **Code Style**
   - Settings > Editor > Code Style > Kotlin
   - Line length: 120
   - Enable Kotlin linter

2. **Plugins**
   - Kotlin Plugin (built-in)
   - Gradle Build Tools
   - Firebase Assistant
   - Material Design Icons
   - Retrofit 2 Support

3. **Debugger Configuration**
   - Enable breakpoints
   - Evaluate expressions
   - Android Studio Profiler

### VS Code (Backend)

**Extensions**
- PHP Intelephense
- PHP Debug
- MySQL
- REST Client
- Thunder Client

**Settings**
```json
{
  "php.validate.executablePath": "/usr/local/bin/php",
  "[php]": {
    "editor.defaultFormatter": "bmewburn.vscode-intelephense-client",
    "editor.formatOnSave": true
  }
}
```

## Running Services

### Start All Services

```bash
#!/bin/bash
# start-services.sh

echo "Starting MySQL..."
brew services start mysql@8.0

echo "Starting PHP Server..."
cd backend && php -S localhost:8000 -t public/ &

echo "Starting Admin Panel..."
cd admin && npm start &

echo "\nAll services started!"
echo "Backend: http://localhost:8000"
echo "Admin: http://localhost:3000"
echo "Database: localhost:3306"
```

### Docker Setup (Alternative)

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: transport_connect
      MYSQL_USER: transport_user
      MYSQL_PASSWORD: secure_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  php:
    build:
      dockerfile: Dockerfile.php
    ports:
      - "8000:8000"
    volumes:
      - ./backend:/var/www
    environment:
      - DB_HOST=mysql
    depends_on:
      - mysql

  admin:
    build:
      dockerfile: Dockerfile.admin
    ports:
      - "3000:3000"
    volumes:
      - ./admin:/app
    environment:
      - REACT_APP_API_URL=http://localhost:8000/api

volumes:
  mysql_data:
```

```bash
# Run with Docker
docker-compose up -d
```

## Testing

### Backend Testing

```bash
cd backend

# Install PHPUnit
composer require phpunit/phpunit

# Run tests
./vendor/bin/phpunit tests/
```

### Android Testing

```bash
cd android/UserApp

# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedDebugAndroidTest
```

## Troubleshooting

### PHP Connection Issues

```bash
# Check PHP installation
php -v

# Check installed extensions
php -m | grep mysql

# Restart MySQL
sudo systemctl restart mysql
```

### Android Gradle Issues

```bash
# Clear Gradle cache
./gradlew clean

# Rebuild project
./gradlew build

# Check dependencies
./gradlew dependencies
```

### Database Connection Errors

```bash
# Test MySQL connection
mysql -u transport_user -p -h localhost

# Check MySQL service
sudo systemctl status mysql

# View MySQL logs
tail -f /var/log/mysql/error.log
```

## Next Steps

1. Read [API Documentation](./documentation/API.md)
2. Review [Database Schema](./documentation/DATABASE.md)
3. Study [Clean Architecture Guide](./documentation/ARCHITECTURE.md)
4. Check [Security Best Practices](./documentation/SECURITY.md)
5. Explore code examples in each module

## Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [PHP 8.3 Documentation](https://www.php.net/docs.php)
- [MySQL 8.0 Documentation](https://dev.mysql.com/doc/)
- [JWT.io](https://jwt.io/)
- [Firebase Documentation](https://firebase.google.com/docs)
