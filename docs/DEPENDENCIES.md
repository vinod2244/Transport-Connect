# AP Transport Connect - Project Dependencies

## 📚 Backend Dependencies (PHP)

### Core Framework
```
laravel/framework:^11.0
laravel/sanctum:^4.0
laravel/tinker:^2.0
```

### Authentication & Authorization
```
firebase/php-jwt:^6.8
spatie/laravel-permission:^6.0
```

### Database
```
laravel/framework (Eloquent ORM)
doctrine/dbal:^3.6
```

### Caching
```
predis/predis:^2.1 (Redis client)
```

### External APIs
```
google/apiclient:^2.50 (Google Maps API)
kreait/firebase-php:^7.0 (Firebase)
razorpay/razorpay:^2.8 (Payment Gateway)
twilio/sdk:^8.0 (SMS)
aws/aws-sdk-php:^3.300 (S3 Storage)
```

### HTTP Client
```
guzzlehttp/guzzle:^7.5
symfony/http-client:^7.0
```

### API Documentation
```
knuckleswtf/scribe:^4.0
```

### CORS
```
barryvdh/laravel-cors:^2.1
```

### Utilities
```
spatie/laravel-settings:^4.0
spatie/laravel-query-builder:^5.0
spatie/laravel-activitylog:^4.7
monolog/monolog:^3.0
phpoffice/phpmailer:^6.8
```

### Development
```
fakerphp/faker:^1.21
laravel/pint:^1.12
mockery/mockery:^1.5
phpunit/phpunit:^10.5
spatie/laravel-ray:^1.32
```

---

## 📱 Android Dependencies

### Core Android
```kotlin
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
```

### Jetpack Compose
```kotlin
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.navigation:navigation-compose:2.7.5")
```

### Database
```kotlin
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
```

### Network
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.10.0")
implementation("com.squareup.retrofit2:converter-gson:2.10.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

### Dependency Injection
```kotlin
implementation("com.google.dagger:hilt-android:2.48")
```

### Location & Maps
```kotlin
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
```

### Firebase
```kotlin
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
```

### Coroutines
```kotlin
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### Image Loading
```kotlin
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("com.github.bumptech.glide:glide:4.15.1")
```

### Testing
```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
```

---

## 🌐 Admin Panel Dependencies

### Framework
```json
"@vitejs/plugin-vue": "^5.0.0",
"vue": "^3.3.0",
"vite": "^5.0.0"
```

### UI Framework
```json
"bootstrap": "^5.3.0",
"bootstrap-vue-3": "^0.6.0"
```

### State Management
```json
"pinia": "^2.1.0",
"vuex": "^4.1.0"
```

### Routing
```json
"vue-router": "^4.2.0"
```

### HTTP Client
```json
"axios": "^1.6.0"
```

### Form Handling
```json
"vee-validate": "^4.11.0",
"yup": "^1.3.0"
```

### Date & Time
```json
"date-fns": "^2.30.0",
"dayjs": "^1.11.0"
```

### Charts
```json
"chart.js": "^4.4.0",
"vue-chartjs": "^5.2.0"
```

### Icons
```json
"bootstrap-icons": "^1.11.0"
```

### Testing
```json
"@testing-library/vue": "^8.0.0",
"@testing-library/jest-dom": "^6.1.0",
"vitest": "^0.34.0"
```

---

## 🔧 Development Tools

### Backend
- **IDE**: PHPStorm / VS Code
- **Debugger**: Xdebug
- **Package Manager**: Composer
- **Version Control**: Git
- **Documentation**: PHPDoc

### Android
- **IDE**: Android Studio 2023.1+
- **Build Tool**: Gradle 8.2.0+
- **Debugger**: Android Debugger
- **Emulator**: Android Emulator / Physical Device
- **Package Manager**: Gradle, Maven

### Admin Panel
- **IDE**: VS Code
- **Package Manager**: npm / yarn
- **Build Tool**: Vite
- **Debugger**: Chrome DevTools
- **Documentation**: Storybook

---

## 🌍 System Dependencies

### Server (Ubuntu 22.04)
```
PHP 8.3+
MySQL 8.0+
Redis 6.0+
Nginx 1.18+
Node.js 18+
Git 2.34+
```

### Development Machine
```
Android Studio 2023.1+
Docker (optional)
Docker Compose (optional)
Postman/Insomnia (API testing)
Git
```

---

## 📦 Version Specifications

### Critical Versions
| Component | Min Version | Recommended | Max Version |
|-----------|------------|-------------|-------------|
| PHP | 8.3 | 8.3.10 | 8.3.x |
| MySQL | 8.0 | 8.0.35 | 8.0.x |
| Redis | 6.0 | 7.2 | 7.x |
| Node.js | 18 | 20 | 20.x |
| Kotlin | 1.9 | 1.9.10 | 1.9.x |
| Android SDK | 24 | 34 | 34+ |

---

**Last Updated**: 2024
