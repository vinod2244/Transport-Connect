# Android App - File Structure & Architecture

## Project Overview

The Android app follows **Clean Architecture** with **MVVM** pattern:

- **Presentation Layer**: UI (Activities, Fragments, ViewModels)
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Repositories and data sources

## Directory Structure

```
UserApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/aptransportconnect/user/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   │   │   ├── BookingDao.kt
│   │   │   │   │   │   │   │   └── PaymentDao.kt
│   │   │   │   │   │   │   └── entities/
│   │   │   │   │   │   │       ├── UserEntity.kt
│   │   │   │   │   │   │       ├── BookingEntity.kt
│   │   │   │   │   │   │       └── PaymentEntity.kt
│   │   │   │   │   │   └── preferences/
│   │   │   │   │   │       ├── PreferenceManager.kt
│   │   │   │   │   │       └── EncryptedDataStore.kt
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   ├── AuthApiService.kt
│   │   │   │   │   │   │   ├── UserApiService.kt
│   │   │   │   │   │   │   ├── BookingApiService.kt
│   │   │   │   │   │   │   ├── VehicleApiService.kt
│   │   │   │   │   │   │   ├── PaymentApiService.kt
│   │   │   │   │   │   │   ├── TrackingApiService.kt
│   │   │   │   │   │   │   ├── ChatApiService.kt
│   │   │   │   │   │   │   └── RatingApiService.kt
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   │   ├── LoginRequest.kt
│   │   │   │   │   │   │   │   ├── RegisterRequest.kt
│   │   │   │   │   │   │   │   ├── CreateBookingRequest.kt
│   │   │   │   │   │   │   │   └── PaymentInitiateRequest.kt
│   │   │   │   │   │   │   └── response/
│   │   │   │   │   │   │       ├── ApiResponse.kt
│   │   │   │   │   │   │       ├── UserResponse.kt
│   │   │   │   │   │   │       ├── BookingResponse.kt
│   │   │   │   │   │   │       ├── VehicleResponse.kt
│   │   │   │   │   │   │       └── PaymentResponse.kt
│   │   │   │   │   │   ├── interceptor/
│   │   │   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   │   │   ├── LoggingInterceptor.kt
│   │   │   │   │   │   │   └── ErrorInterceptor.kt
│   │   │   │   │   │   └── mapper/
│   │   │   │   │   │       ├── UserMapper.kt
│   │   │   │   │   │       ├── BookingMapper.kt
│   │   │   │   │   │       └── VehicleMapper.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── AuthRepository.kt
│   │   │   │   │       ├── UserRepository.kt
│   │   │   │   │       ├── BookingRepository.kt
│   │   │   │   │       ├── VehicleRepository.kt
│   │   │   │   │       ├── PaymentRepository.kt
│   │   │   │   │       ├── TrackingRepository.kt
│   │   │   │   │       ├── ChatRepository.kt
│   │   │   │   │       └── RatingRepository.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   ├── Booking.kt
│   │   │   │   │   │   ├── Vehicle.kt
│   │   │   │   │   │   ├── Payment.kt
│   │   │   │   │   │   ├── Driver.kt
│   │   │   │   │   │   └── Rating.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── IAuthRepository.kt
│   │   │   │   │   │   ├── IUserRepository.kt
│   │   │   │   │   │   ├── IBookingRepository.kt
│   │   │   │   │   │   └── ...
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── auth/
│   │   │   │   │       │   ├── LoginUseCase.kt
│   │   │   │   │       │   ├── RegisterUseCase.kt
│   │   │   │   │       │   ├── VerifyOtpUseCase.kt
│   │   │   │   │       │   └── LogoutUseCase.kt
│   │   │   │   │       ├── booking/
│   │   │   │   │       │   ├── SearchVehiclesUseCase.kt
│   │   │   │   │       │   ├── CreateBookingUseCase.kt
│   │   │   │   │       │   ├── GetBookingsUseCase.kt
│   │   │   │   │       │   ├── CancelBookingUseCase.kt
│   │   │   │   │       │   └── TrackBookingUseCase.kt
│   │   │   │   │       ├── payment/
│   │   │   │   │       │   ├── InitiatePaymentUseCase.kt
│   │   │   │   │       │   ├── VerifyPaymentUseCase.kt
│   │   │   │   │       │   └── GetPaymentHistoryUseCase.kt
│   │   │   │   │       └── user/
│   │   │   │   │           ├── GetProfileUseCase.kt
│   │   │   │   │           ├── UpdateProfileUseCase.kt
│   │   │   │   │           └── GetWalletUseCase.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── ui/
│   │   │   │   │   │   ├── screen/
│   │   │   │   │   │   │   ├── splash/
│   │   │   │   │   │   │   │   ├── SplashScreen.kt
│   │   │   │   │   │   │   │   └── SplashViewModel.kt
│   │   │   │   │   │   │   ├── onboarding/
│   │   │   │   │   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   │   │   │   │   └── OnboardingViewModel.kt
│   │   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   │   ├── login/
│   │   │   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   │   │   │   │   ├── register/
│   │   │   │   │   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   │   │   │   │   └── RegisterViewModel.kt
│   │   │   │   │   │   │   │   ├── otp/
│   │   │   │   │   │   │   │   │   ├── OtpScreen.kt
│   │   │   │   │   │   │   │   │   └── OtpViewModel.kt
│   │   │   │   │   │   │   │   └── forgot_password/
│   │   │   │   │   │   │   │       ├── ForgotPasswordScreen.kt
│   │   │   │   │   │   │   │       └── ForgotPasswordViewModel.kt
│   │   │   │   │   │   │   ├── home/
│   │   │   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   │   │   │   └── components/
│   │   │   │   │   │   │   │       ├── TopBar.kt
│   │   │   │   │   │   │   │       ├── SearchBar.kt
│   │   │   │   │   │   │   │       └── QuickActions.kt
│   │   │   │   │   │   │   ├── search/
│   │   │   │   │   │   │   │   ├── SearchScreen.kt
│   │   │   │   │   │   │   │   ├── SearchViewModel.kt
│   │   │   │   │   │   │   │   ├── SearchResultScreen.kt
│   │   │   │   │   │   │   │   └── adapter/
│   │   │   │   │   │   │   │       └── VehicleListAdapter.kt
│   │   │   │   │   │   │   ├── booking/
│   │   │   │   │   │   │   │   ├── BookingDetailScreen.kt
│   │   │   │   │   │   │   │   ├── BookingConfirmationScreen.kt
│   │   │   │   │   │   │   │   └── BookingViewModel.kt
│   │   │   │   │   │   │   ├── tracking/
│   │   │   │   │   │   │   │   ├── TrackingScreen.kt
│   │   │   │   │   │   │   │   ├── TrackingViewModel.kt
│   │   │   │   │   │   │   │   └── components/
│   │   │   │   │   │   │   │       └── MapView.kt
│   │   │   │   │   │   │   ├── payment/
│   │   │   │   │   │   │   │   ├── PaymentScreen.kt
│   │   │   │   │   │   │   │   └── PaymentViewModel.kt
│   │   │   │   │   │   │   ├── chat/
│   │   │   │   │   │   │   │   ├── ChatListScreen.kt
│   │   │   │   │   │   │   │   ├── ChatDetailScreen.kt
│   │   │   │   │   │   │   │   └── ChatViewModel.kt
│   │   │   │   │   │   │   ├── wallet/
│   │   │   │   │   │   │   │   ├── WalletScreen.kt
│   │   │   │   │   │   │   │   ├── AddMoneyScreen.kt
│   │   │   │   │   │   │   │   └── WalletViewModel.kt
│   │   │   │   │   │   │   ├── profile/
│   │   │   │   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   │   │   │   ├── EditProfileScreen.kt
│   │   │   │   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   │   │   │   ├── support/
│   │   │   │   │   │   │   │   ├── SupportScreen.kt
│   │   │   │   │   │   │   │   └── SupportViewModel.kt
│   │   │   │   │   │   │   └── settings/
│   │   │   │   │   │   │       ├── SettingsScreen.kt
│   │   │   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   │   │   ├── fragment/
│   │   │   │   │   │   │   └── (Fragments if needed)
│   │   │   │   │   │   ├── activity/
│   │   │   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │   │   └── AuthActivity.kt
│   │   │   │   │   │   ├── adapter/
│   │   │   │   │   │   │   ├── BookingHistoryAdapter.kt
│   │   │   │   │   │   │   ├── ChatAdapter.kt
│   │   │   │   │   │   │   ├── NotificationAdapter.kt
│   │   │   │   │   │   │   └── PaymentHistoryAdapter.kt
│   │   │   │   │   │   └── component/
│   │   │   │   │   │       ├── BottomNavBar.kt
│   │   │   │   │   │       ├── LoadingDialog.kt
│   │   │   │   │   │       ├── ErrorSnackbar.kt
│   │   │   │   │   │       ├── ConfirmDialog.kt
│   │   │   │   │   │       └── RatingBar.kt
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   ├── BaseViewModel.kt
│   │   │   │   │   │   └── (Specific ViewModels in screens)
│   │   │   │   │   ├── state/
│   │   │   │   │   │   ├── UiState.kt
│   │   │   │   │   │   ├── AuthState.kt
│   │   │   │   │   │   ├── BookingState.kt
│   │   │   │   │   │   └── PaymentState.kt
│   │   │   │   │   └── event/
│   │   │   │   │       ├── UiEvent.kt
│   │   │   │   │       ├── AuthEvent.kt
│   │   │   │   │       ├── BookingEvent.kt
│   │   │   │   │       └── PaymentEvent.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   └── UseCaseModule.kt
│   │   │   │   ├── services/
│   │   │   │   │   ├── FirebaseMessagingService.kt
│   │   │   │   │   ├── LocationService.kt
│   │   │   │   │   └── SyncService.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Constants.kt
│   │   │   │   │   ├── Extensions.kt
│   │   │   │   │   ├── PreferenceManager.kt
│   │   │   │   │   ├── DateUtils.kt
│   │   │   │   │   ├── NumberUtils.kt
│   │   │   │   │   ├── ValidationUtils.kt
│   │   │   │   │   └── Logger.kt
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiClient.kt
│   │   │   │   │   ├── NetworkState.kt
│   │   │   │   │   └── ConnectivityManager.kt
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Typography.kt
│   │   │   │   │   ├── Dimen.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   └── App.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── (For Fragment-based layouts if needed)
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_home.xml
│   │   │   │   │   ├── ic_search.xml
│   │   │   │   │   ├── ic_booking.xml
│   │   │   │   │   ├── ic_payment.xml
│   │   │   │   │   ├── ic_profile.xml
│   │   │   │   │   └── ... (All vector drawables)
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   ├── values-night/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   ├── anim/
│   │   │   │   │   ├── fade_in.xml
│   │   │   │   │   ├── slide_up.xml
│   │   │   │   │   └── zoom_in.xml
│   │   │   │   └── raw/
│   │   │   │       └── (Lottie animations)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── google-services.json
├── build.gradle.kts (Project level)
├── settings.gradle.kts
├── gradle.properties
└── local.properties.example
```

## Key Components Explanation

### Data Layer

- **Local**: Room database, SharedPreferences, DataStore
- **Remote**: Retrofit API services, DTOs
- **Repository**: Combines local and remote data sources

### Domain Layer

- **Model**: Pure domain models (independent of data layer)
- **Repository Interfaces**: Defines repository contracts
- **UseCase**: Business logic (single responsibility)

### Presentation Layer

- **UI/Screen**: Composable screens (Jetpack Compose)
- **ViewModel**: Manages UI state and events
- **State/Event**: Manages UI state and user interactions
- **Adapter**: RecyclerView adapters
- **Component**: Reusable UI components

### Dependency Injection

- **Hilt**: Provides dependency injection
- **Modules**: Provides instances of dependencies

## Code Organization Principles

1. **Separation of Concerns**: Each layer has specific responsibility
2. **Dependency Inversion**: Depend on abstractions, not implementations
3. **Single Responsibility**: Each class has one reason to change
4. **Open/Closed**: Open for extension, closed for modification
5. **Testability**: Easy to write unit and integration tests

## Navigation Structure

- **Authentication Flow**: Login → Register → OTP → Home
- **Main Flow**: Home → Search → Booking → Payment → Tracking
- **Bottom Navigation**: Home, Search, Bookings, Chat, Profile
- **Deep Linking**: Support deep links for notifications

## Data Flow

```
UI (Compose) 
  ↓
ViewModel (State Management)
  ↓
UseCase (Business Logic)
  ↓
Repository (Data Access)
  ↓
Local/Remote Data Sources
  ↓
Database/API
```

This architecture ensures maintainability, testability, and scalability.
