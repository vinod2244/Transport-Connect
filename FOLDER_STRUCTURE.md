# Directory Structure Guide

```
Transport-Connect/
│
├── 📁 backend/                          # PHP 8.3 REST API
│   ├── app/
│   │   ├── Models/
│   │   │   ├── User.php
│   │   │   ├── Booking.php
│   │   │   ├── Driver.php
│   │   │   ├── Vehicle.php
│   │   │   ├── Payment.php
│   │   │   ├── Rating.php
│   │   │   └── Notification.php
│   │   │
│   │   ├── Controllers/
│   │   │   ├── Auth/
│   │   │   │   ├── LoginController.php
│   │   │   │   ├── RegisterController.php
│   │   │   │   └── RefreshTokenController.php
│   │   │   ├── Customer/
│   │   │   │   ├── BookingController.php
│   │   │   │   ├── ProfileController.php
│   │   │   │   └── ReviewController.php
│   │   │   ├── Driver/
│   │   │   │   ├── AvailabilityController.php
│   │   │   │   ├── LocationController.php
│   │   │   │   ├── EarningsController.php
│   │   │   │   └── RatingController.php
│   │   │   ├── VehicleOwner/
│   │   │   │   ├── VehicleController.php
│   │   │   │   ├── DriverController.php
│   │   │   │   └── AnalyticsController.php
│   │   │   └── Admin/
│   │   │       ├── UserManagementController.php
│   │   │       ├── BookingManagementController.php
│   │   │       ├── PaymentManagementController.php
│   │   │       ├── ReportController.php
│   │   │       └── SettingsController.php
│   │   │
│   │   ├── Services/
│   │   │   ├── AuthService.php
│   │   │   ├── BookingService.php
│   │   │   ├── PaymentService.php
│   │   │   ├── NotificationService.php
│   │   │   ├── LocationService.php
│   │   │   ├── RatingService.php
│   │   │   ├── SmsService.php
│   │   │   ├── EmailService.php
│   │   │   └── ReportService.php
│   │   │
│   │   ├── Repositories/
│   │   │   ├── UserRepository.php
│   │   │   ├── BookingRepository.php
│   │   │   ├── DriverRepository.php
│   │   │   ├── VehicleRepository.php
│   │   │   └── PaymentRepository.php
│   │   │
│   │   ├── Requests/
│   │   │   ├── CreateBookingRequest.php
│   │   │   ├── UpdateProfileRequest.php
│   │   │   ├── CreatePaymentRequest.php
│   │   │   └── RateBookingRequest.php
│   │   │
│   │   ├── Responses/
│   │   │   ├── ApiResponse.php
│   │   │   ├── ErrorResponse.php
│   │   │   └── PaginatedResponse.php
│   │   │
│   │   ├── Middleware/
│   │   │   ├── JwtAuthenticate.php
│   │   │   ├── RoleAuthorize.php
│   │   │   ├── CheckBookingOwner.php
│   │   │   ├── RateLimitMiddleware.php
│   │   │   └── LogRequests.php
│   │   │
│   │   ├── Exceptions/
│   │   │   ├── BookingNotFoundException.php
│   │   │   ├── DriverNotAvailableException.php
│   │   │   ├── InsufficientFundsException.php
│   │   │   ├── UnauthorizedException.php
│   │   │   └── ValidationException.php
│   │   │
│   │   ├── Jobs/
│   │   │   ├── SendNotificationJob.php
│   │   │   ├── ProcessPaymentJob.php
│   │   │   ├── UpdateLocationJob.php
│   │   │   └── GenerateReportJob.php
│   │   │
│   │   ├── Events/
│   │   │   ├── BookingCreated.php
│   │   │   ├── BookingAccepted.php
│   │   │   ├── PaymentProcessed.php
│   │   │   └── RatingSubmitted.php
│   │   │
│   │   ├── Listeners/
│   │   │   ├── SendBookingNotification.php
│   │   │   ├── UpdateDriverLocation.php
│   │   │   └── SendPaymentConfirmation.php
│   │   │
│   │   ├── Traits/
│   │   │   ├── HasUuid.php
│   │   │   ├── HasTimestamps.php
│   │   │   ├── Filterable.php
│   │   │   └── Sortable.php
│   │   │
│   │   ├── Helpers/
│   │   │   ├── ResponseHelper.php
│   │   │   ├── LocationHelper.php
│   │   │   ├── PricingHelper.php
│   │   │   └── ValidationHelper.php
│   │   │
│   │   └── Providers/
│   │       ├── AppServiceProvider.php
│   │       ├── AuthServiceProvider.php
│   │       ├── EventServiceProvider.php
│   │       └── RouteServiceProvider.php
│   │
│   ├── config/
│   │   ├── app.php
│   │   ├── auth.php
│   │   ├── database.php
│   │   ├── cache.php
│   │   ├── queue.php
│   │   ├── jwt.php
│   │   ├── firebase.php
│   │   └── razorpay.php
│   │
│   ├── database/
│   │   ├── migrations/
│   │   │   ├── 2024_01_01_000001_create_users_table.php
│   │   │   ├── 2024_01_01_000002_create_vehicles_table.php
│   │   │   ├── 2024_01_01_000003_create_bookings_table.php
│   │   │   ├── 2024_01_01_000004_create_payments_table.php
│   │   │   └── ...
│   │   ├── seeders/
│   │   │   ├── DatabaseSeeder.php
│   │   │   ├── RoleSeeder.php
│   │   │   ├── AdminSeeder.php
│   │   │   └── VehicleTypeSeeder.php
│   │   └── factories/
│   │       ├── UserFactory.php
│   │       ├── BookingFactory.php
│   │       └── DriverFactory.php
│   │
│   ├── routes/
│   │   ├── api.php
│   │   ├── auth.php
│   │   ├── customer.php
│   │   ├── driver.php
│   │   ├── vehicle_owner.php
│   │   ├── admin.php
│   │   └── health.php
│   │
│   ├── storage/
│   │   ├── logs/
│   │   ├── app/
│   │   │   ├── documents/
│   │   │   ├── photos/
│   │   │   └── invoices/
│   │   └── framework/
│   │
│   ├── tests/
│   │   ├── Unit/
│   │   │   ├── Services/
│   │   │   └── Helpers/
│   │   ├── Feature/
│   │   │   ├── Auth/
│   │   │   ├── Booking/
│   │   │   └── Payment/
│   │   └── TestCase.php
│   │
│   ├── .env.example
│   ├── composer.json
│   ├── artisan
│   ├── phpunit.xml
│   └── README.md
│
├── 📁 android/                          # Kotlin Android App
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/aptransportconnect/
│   │   │   │   │       ├── di/
│   │   │   │   │       │   ├── AppModule.kt
│   │   │   │   │       │   ├── RepositoryModule.kt
│   │   │   │   │       │   └── ViewModelModule.kt
│   │   │   │   │       │
│   │   │   │   │       ├── data/
│   │   │   │   │       │   ├── local/
│   │   │   │   │       │   │   ├── database/
│   │   │   │   │       │   │   │   ├── AppDatabase.kt
│   │   │   │   │       │   │   │   ├── dao/
│   │   │   │   │       │   │   │   │   ├── BookingDao.kt
│   │   │   │   │       │   │   │   │   └── DriverDao.kt
│   │   │   │   │       │   │   │   └── entity/
│   │   │   │   │       │   │   │       ├── BookingEntity.kt
│   │   │   │   │       │   │   │       └── DriverEntity.kt
│   │   │   │   │       │   │   └── preferences/
│   │   │   │   │       │   │       └── UserPreferences.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   ├── remote/
│   │   │   │   │       │   │   ├── api/
│   │   │   │   │       │   │   │   ├── ApiClient.kt
│   │   │   │   │       │   │   │   ├── AuthService.kt
│   │   │   │   │       │   │   │   ├── BookingService.kt
│   │   │   │   │       │   │   │   ├── DriverService.kt
│   │   │   │   │       │   │   │   └── PaymentService.kt
│   │   │   │   │       │   │   ├── dto/
│   │   │   │   │       │   │   │   ├── BookingDTO.kt
│   │   │   │   │       │   │   │   ├── UserDTO.kt
│   │   │   │   │       │   │   │   └── PaymentDTO.kt
│   │   │   │   │       │   │   └── datasource/
│   │   │   │   │       │   │       ├── RemoteAuthDataSource.kt
│   │   │   │   │       │   │       ├── RemoteBookingDataSource.kt
│   │   │   │   │       │   │       └── RemoteDriverDataSource.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   └── repository/
│   │   │   │   │       │       ├── AuthRepositoryImpl.kt
│   │   │   │   │       │       ├── BookingRepositoryImpl.kt
│   │   │   │   │       │       └── DriverRepositoryImpl.kt
│   │   │   │   │       │
│   │   │   │   │       ├── domain/
│   │   │   │   │       │   ├── entity/
│   │   │   │   │       │   │   ├── Booking.kt
│   │   │   │   │       │   │   ├── Driver.kt
│   │   │   │   │       │   │   ├── User.kt
│   │   │   │   │       │   │   └── Vehicle.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   ├── repository/
│   │   │   │   │       │   │   ├── AuthRepository.kt
│   │   │   │   │       │   │   ├── BookingRepository.kt
│   │   │   │   │       │   │   ├── DriverRepository.kt
│   │   │   │   │       │   │   └── LocationRepository.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   └── usecase/
│   │   │   │   │       │       ├── auth/
│   │   │   │   │       │       │   ├── LoginUseCase.kt
│   │   │   │   │       │       │   ├── RegisterUseCase.kt
│   │   │   │   │       │       │   └── LogoutUseCase.kt
│   │   │   │   │       │       ├── booking/
│   │   │   │   │       │       │   ├── GetBookingsUseCase.kt
│   │   │   │   │       │       │   ├── CreateBookingUseCase.kt
│   │   │   │   │       │       │   └── CancelBookingUseCase.kt
│   │   │   │   │       │       └── driver/
│   │   │   │   │       │           ├── GetNearbyDriversUseCase.kt
│   │   │   │   │       │           └── TrackDriverUseCase.kt
│   │   │   │   │       │
│   │   │   │   │       ├── presentation/
│   │   │   │   │       │   ├── ui/
│   │   │   │   │       │   │   ├── auth/
│   │   │   │   │       │   │   │   ├── LoginActivity.kt
│   │   │   │   │       │   │   │   ├── RegisterActivity.kt
│   │   │   │   │       │   │   │   └── VerificationActivity.kt
│   │   │   │   │       │   │   ├── booking/
│   │   │   │   │       │   │   │   ├── BookingActivity.kt
│   │   │   │   │       │   │   │   ├── BookingListFragment.kt
│   │   │   │   │       │   │   │   ├── BookingDetailFragment.kt
│   │   │   │   │       │   │   │   └── BookingConfirmationFragment.kt
│   │   │   │   │       │   │   ├── tracking/
│   │   │   │   │       │   │   │   ├── TrackingActivity.kt
│   │   │   │   │       │   │   │   └── MapFragment.kt
│   │   │   │   │       │   │   ├── profile/
│   │   │   │   │       │   │   │   ├── ProfileFragment.kt
│   │   │   │   │       │   │   │   ├── EditProfileActivity.kt
│   │   │   │   │       │   │   │   └── PaymentMethodsFragment.kt
│   │   │   │   │       │   │   ├── home/
│   │   │   │   │       │   │   │   └── HomeFragment.kt
│   │   │   │   │       │   │   └── common/
│   │   │   │   │       │   │       ├── MainActivity.kt
│   │   │   │   │       │   │       ├── SplashActivity.kt
│   │   │   │   │       │   │       └── NavigationActivity.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   ├── viewmodel/
│   │   │   │   │       │   │   ├── AuthViewModel.kt
│   │   │   │   │       │   │   ├── BookingViewModel.kt
│   │   │   │   │       │   │   ├── TrackingViewModel.kt
│   │   │   │   │       │   │   └── ProfileViewModel.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   ├── adapter/
│   │   │   │   │       │   │   ├── BookingAdapter.kt
│   │   │   │   │       │   │   ├── DriverListAdapter.kt
│   │   │   │   │       │   │   └── PaymentMethodAdapter.kt
│   │   │   │   │       │   │
│   │   │   │   │       │   └── state/
│   │   │   │   │       │       ├── AuthState.kt
│   │   │   │   │       │       ├── BookingState.kt
│   │   │   │   │       │       └── TrackingState.kt
│   │   │   │   │       │
│   │   │   │   │       └── utils/
│   │   │   │   │           ├── Constants.kt
│   │   │   │   │           ├── Extensions.kt
│   │   │   │   │           ├── LocationManager.kt
│   │   │   │   │           ├── NotificationManager.kt
│   │   │   │   │           ├── PreferencesManager.kt
│   │   │   │   │           ├── SharedPreferencesHelper.kt
│   │   │   │   │           └── DateTimeUtils.kt
│   │   │   │   │
│   │   │   │   ├── res/
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── strings.xml
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   ├── dimens.xml
│   │   │   │   │   │   ├── styles.xml
│   │   │   │   │   │   └── config.xml
│   │   │   │   │   ├── layout/
│   │   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   │   ├── fragment_booking.xml
│   │   │   │   │   │   ├── fragment_tracking.xml
│   │   │   │   │   │   └── ...
│   │   │   │   │   ├── drawable/
│   │   │   │   │   ├── drawable-hdpi/
│   │   │   │   │   ├── drawable-mdpi/
│   │   │   │   │   ├── drawable-xhdpi/
│   │   │   │   │   ├── drawable-xxhdpi/
│   │   │   │   │   ├── drawable-xxxhdpi/
│   │   │   │   │   ├── mipmap-*/
│   │   │   │   │   └── font/
│   │   │   │   │
│   │   │   │   └── AndroidManifest.xml
│   │   │   │
│   │   │   └── test/
│   │   │       ├── kotlin/
│   │   │       │   └── com/aptransportconnect/
│   │   │       │       ├── viewmodel/
│   │   │       │       ├── domain/
│   │   │       │       └── data/
│   │   │       └── resources/
│   │   │
│   │   ├── build.gradle.kts
│   │   └── proguard-rules.pro
│   │
│   ├── gradle/
│   │   └── wrapper/
│   │
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── local.properties.example
│   └── README.md
│
├── 📁 admin/                            # Bootstrap 5 Admin Panel
│   ├── src/
│   │   ├── pages/
│   │   │   ├── auth/
│   │   │   │   ├── Login.vue
│   │   │   │   └── Register.vue
│   │   │   ├── dashboard/
│   │   │   │   ├── Dashboard.vue
│   │   │   │   ├── Statistics.vue
│   │   │   │   └── Charts.vue
│   │   │   ├── bookings/
│   │   │   │   ├── BookingList.vue
│   │   │   │   ├── BookingDetail.vue
│   │   │   │   └── BookingForm.vue
│   │   │   ├── users/
│   │   │   │   ├── UserList.vue
│   │   │   │   ├── UserDetail.vue
│   │   │   │   ├── UserForm.vue
│   │   │   │   └── UserVerification.vue
│   │   │   ├── drivers/
│   │   │   │   ├── DriverList.vue
│   │   │   │   ├── DriverDetail.vue
│   │   │   │   └── DriverApproval.vue
│   │   │   ├── payments/
│   │   │   │   ├── PaymentList.vue
│   │   │   │   ├── PaymentDetail.vue
│   │   │   │   └── RefundManagement.vue
│   │   │   ├── reports/
│   │   │   │   ├── RevenueReport.vue
│   │   │   │   ├── UserReport.vue
│   │   │   │   ├── DriverReport.vue
│   │   │   │   └── BookingReport.vue
│   │   │   ├── settings/
│   │   │   │   ├── GeneralSettings.vue
│   │   │   │   ├── PaymentSettings.vue
│   │   │   │   ├── NotificationSettings.vue
│   │   │   │   └── SystemSettings.vue
│   │   │   └── support/
│   │   │       ├── TicketList.vue
│   │   │       └── TicketDetail.vue
│   │   │
│   │   ├── components/
│   │   │   ├── common/
│   │   │   │   ├── Navbar.vue
│   │   │   │   ├── Sidebar.vue
│   │   │   │   ├── Footer.vue
│   │   │   │   └── Breadcrumb.vue
│   │   │   ├── table/
│   │   │   │   ├── DataTable.vue
│   │   │   │   ├── SearchBar.vue
│   │   │   │   └── Pagination.vue
│   │   │   ├── form/
│   │   │   │   ├── FormInput.vue
│   │   │   │   ├── FormSelect.vue
│   │   │   │   ├── FormTextarea.vue
│   │   │   │   └── FormDatePicker.vue
│   │   │   ├── modal/
│   │   │   │   ├── ConfirmModal.vue
│   │   │   │   ├── FormModal.vue
│   │   │   │   └── DetailModal.vue
│   │   │   └── chart/
│   │   │       ├── LineChart.vue
│   │   │       ├── BarChart.vue
│   │   │       ├── PieChart.vue
│   │   │       └── AreaChart.vue
│   │   │
│   │   ├── stores/
│   │   │   ├── auth.js
│   │   │   ├── users.js
│   │   │   ├── bookings.js
│   │   │   ├── payments.js
│   │   │   └── notifications.js
│   │   │
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   ├── authService.js
│   │   │   ├── bookingService.js
│   │   │   ├── userService.js
│   │   │   ├── paymentService.js
│   │   │   └── reportService.js
│   │   │
│   │   ├── utils/
│   │   │   ├── constants.js
│   │   │   ├── helpers.js
│   │   │   ├── validators.js
│   │   │   └── dateUtils.js
│   │   │
│   │   ├── assets/
│   │   │   ├── css/
│   │   │   │   ├── main.css
│   │   │   │   ├── variables.css
│   │   │   │   └── responsive.css
│   │   │   ├── images/
│   │   │   └── icons/
│   │   │
│   │   ├── App.vue
│   │   ├── main.js
│   │   └── router.js
│   │
│   ├── public/
│   │   ├── index.html
│   │   ├── favicon.ico
│   │   └── robots.txt
│   │
│   ├── package.json
│   ├── vite.config.js
│   ├── .env.example
│   └── README.md
│
├── 📁 database/                         # Database Schemas
│   ├── schemas/
│   │   ├── 01_initial_schema.sql
│   │   ├── 02_users_table.sql
│   │   ├── 03_vehicles_table.sql
│   │   ├── 04_bookings_table.sql
│   │   ├── 05_payments_table.sql
│   │   └── complete_schema.sql
│   │
│   ├── seeders/
│   │   ├── roles.sql
│   │   ├── vehicle_types.sql
│   │   ├── admin_user.sql
│   │   └── test_data.sql
│   │
│   └── README.md
│
├── 📁 docs/                             # Documentation
│   ├── README.md
│   ├── ARCHITECTURE.md
│   ├── API.md
│   ├── DATABASE.md
│   ├── CODING_STANDARDS.md
│   ├── SETUP.md
│   ├── DEPLOYMENT.md
│   ├── DEPENDENCIES.md
│   ├── TROUBLESHOOTING.md
│   └── FOLDER_STRUCTURE.md
│
├── 📁 scripts/                          # Utility Scripts
│   ├── deploy.sh
│   ├── setup-db.sh
│   ├── generate-keys.sh
│   ├── backup-db.sh
│   ├── clear-cache.sh
│   └── setup-env.sh
│
├── 📁 .github/                          # GitHub Configuration
│   ├── workflows/
│   │   ├── deploy.yml
│   │   ├── tests.yml
│   │   └── lint.yml
│   └── ISSUE_TEMPLATE/
│
├── .gitignore
├── .gitattributes
├── README.md
├── CONTRIBUTING.md
├── LICENSE
└── docker-compose.yml (optional)
```

---

## 📊 Statistics

- **Backend Controllers**: 15+
- **Backend Services**: 10+
- **Backend Models**: 10+
- **Android Activities/Fragments**: 20+
- **Android ViewModels**: 8+
- **Admin Pages**: 15+
- **Admin Components**: 20+
- **Database Tables**: 15+
- **API Endpoints**: 80+

---

**Last Updated**: 2024
