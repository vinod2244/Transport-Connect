# AP Transport Connect - REST API Documentation

## 🔗 API Overview

- **Base URL**: `https://api.aptransportconnect.com/api/v1`
- **Protocol**: HTTPS only
- **Response Format**: JSON
- **Authentication**: JWT Bearer Token

## 🔐 Authentication

### Login Endpoint

```
POST /auth/login
Content-Type: application/json

Request Body:
{
  "email": "user@example.com",
  "password": "password123"
}

Response (200):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "user@example.com",
      "role": "customer",
      "phone": "+919999999999"
    },
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "token_type": "Bearer",
    "expires_in": 3600
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Register Endpoint

```
POST /auth/register
Content-Type: application/json

Request Body:
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "user@example.com",
  "phone": "+919999999999",
  "password": "password123",
  "password_confirmation": "password123",
  "role": "customer"
}

Response (201):
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "phone": "+919999999999",
    "role": "customer",
    "created_at": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Refresh Token

```
POST /auth/refresh
Authorization: Bearer {refresh_token}

Response (200):
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 3600
  }
}
```

---

## 📱 Booking Endpoints

### List Bookings

```
GET /bookings?status=pending&page=1&per_page=10
Authorization: Bearer {access_token}

Query Parameters:
- status: pending|accepted|in_progress|completed|cancelled
- page: integer (default: 1)
- per_page: integer (default: 10)
- sort_by: created_at|updated_at|status (default: created_at)
- sort_order: asc|desc (default: desc)

Response (200):
{
  "success": true,
  "message": "Bookings retrieved",
  "data": [
    {
      "id": 1,
      "booking_number": "BK-001",
      "customer": {
        "id": 1,
        "name": "John Doe",
        "phone": "+919999999999"
      },
      "driver": {
        "id": 5,
        "name": "Driver Name",
        "phone": "+919999999998",
        "rating": 4.5,
        "vehicle": {
          "id": 2,
          "registration_number": "DL-01-AB-1234",
          "vehicle_type": "Car"
        }
      },
      "pickup_location": "123 Main St, New Delhi",
      "pickup_latitude": 28.6139,
      "pickup_longitude": 77.2090,
      "dropoff_location": "456 Park Ave, New Delhi",
      "dropoff_latitude": 28.6200,
      "dropoff_longitude": 77.2150,
      "status": "in_progress",
      "booking_type": "One-Way",
      "estimated_distance_km": 5.2,
      "estimated_duration_minutes": 15,
      "base_fare": 100,
      "distance_charge": 52,
      "tax_amount": 24.30,
      "total_amount": 176.30,
      "payment_status": "pending",
      "created_at": "2024-01-15T10:30:00Z",
      "updated_at": "2024-01-15T10:35:00Z"
    }
  ],
  "pagination": {
    "current_page": 1,
    "total_pages": 5,
    "per_page": 10,
    "total": 45
  }
}
```

### Create Booking

```
POST /bookings
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "pickup_location": "123 Main St, New Delhi",
  "pickup_latitude": 28.6139,
  "pickup_longitude": 77.2090,
  "dropoff_location": "456 Park Ave, New Delhi",
  "dropoff_latitude": 28.6200,
  "dropoff_longitude": 77.2150,
  "booking_type": "One-Way",
  "vehicle_type": "Car",
  "scheduled_pickup_time": "2024-01-15T11:00:00Z",
  "special_requests": "Please use AC",
  "discount_code": "WELCOME20"
}

Response (201):
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "booking_number": "BK-001",
    "status": "requested",
    "total_amount": 176.30,
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

### Get Booking Details

```
GET /bookings/:id
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Booking retrieved",
  "data": {
    "id": 1,
    "booking_number": "BK-001",
    "customer": { ... },
    "driver": { ... },
    "vehicle": { ... },
    "pickup_location": "123 Main St, New Delhi",
    "dropoff_location": "456 Park Ave, New Delhi",
    "status": "in_progress",
    "total_amount": 176.30,
    "payment_status": "paid",
    "route_stops": [
      {
        "stop_order": 1,
        "stop_location": "Stop 1",
        "arrival_time": "2024-01-15T10:40:00Z"
      }
    ],
    "location_tracking": {
      "current_latitude": 28.6160,
      "current_longitude": 77.2110,
      "estimated_arrival_time": "2024-01-15T10:45:00Z"
    }
  }
}
```

### Cancel Booking

```
POST /bookings/:id/cancel
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "cancellation_reason": "Change of plans"
}

Response (200):
{
  "success": true,
  "message": "Booking cancelled successfully",
  "data": {
    "id": 1,
    "status": "cancelled",
    "refund_amount": 176.30
  }
}
```

---

## 👤 Profile Endpoints

### Get Profile

```
GET /profile
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Profile retrieved",
  "data": {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "user@example.com",
    "phone": "+919999999999",
    "role": "customer",
    "profile_photo_url": "https://...",
    "address": "123 Main St",
    "city": "New Delhi",
    "state": "Delhi",
    "country": "India",
    "postal_code": "110001",
    "date_of_birth": "1990-01-15",
    "gender": "Male",
    "email_verified": true,
    "phone_verified": true,
    "is_active": true,
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

### Update Profile

```
PUT /profile
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+919999999999",
  "address": "123 New Street",
  "city": "Mumbai",
  "state": "Maharashtra"
}

Response (200):
{
  "success": true,
  "message": "Profile updated successfully",
  "data": { ... }
}
```

### Upload Profile Photo

```
POST /profile/photo
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

Request:
- file: (image file, max 5MB)

Response (200):
{
  "success": true,
  "message": "Photo uploaded successfully",
  "data": {
    "photo_url": "https://..."
  }
}
```

---

## 🚗 Driver Endpoints

### Get Available Drivers (for Admin/Dispatcher)

```
GET /drivers/available?latitude=28.6139&longitude=77.2090&radius=5
Authorization: Bearer {access_token}

Query Parameters:
- latitude: float (required)
- longitude: float (required)
- radius: integer (in km, default: 5)
- vehicle_type: car|truck|bus|tempo|auto

Response (200):
{
  "success": true,
  "message": "Available drivers retrieved",
  "data": [
    {
      "id": 5,
      "user_id": 1,
      "name": "Driver Name",
      "phone": "+919999999998",
      "vehicle": {
        "id": 2,
        "registration_number": "DL-01-AB-1234",
        "vehicle_type": "Car",
        "capacity": 4
      },
      "current_location": {
        "latitude": 28.6200,
        "longitude": 77.2150
      },
      "average_rating": 4.5,
      "total_rides": 150,
      "distance_away_km": 1.2,
      "estimated_arrival_time_seconds": 180
    }
  ]
}
```

### Get Driver Profile

```
GET /drivers/:id
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Driver profile retrieved",
  "data": {
    "id": 5,
    "user": {
      "name": "Driver Name",
      "email": "driver@example.com",
      "phone": "+919999999998",
      "profile_photo_url": "https://..."
    },
    "license_number": "DL-1234567890",
    "license_expiry_date": "2025-12-31",
    "vehicle": {
      "registration_number": "DL-01-AB-1234",
      "vehicle_type": "Car"
    },
    "average_rating": 4.5,
    "total_rides": 150,
    "total_earnings": 50000,
    "is_available": true
  }
}
```

### Update Driver Location

```
PUT /drivers/location
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "latitude": 28.6200,
  "longitude": 77.2150,
  "accuracy_meter": 10,
  "speed_kmh": 25.5,
  "heading": 180
}

Response (200):
{
  "success": true,
  "message": "Location updated"
}
```

### Accept Booking (Driver)

```
POST /bookings/:id/accept
Authorization: Bearer {access_token}
Content-Type: application/json

Response (200):
{
  "success": true,
  "message": "Booking accepted",
  "data": {
    "id": 1,
    "booking_number": "BK-001",
    "status": "accepted",
    "customer_phone": "+919999999999",
    "pickup_location": "123 Main St, New Delhi"
  }
}
```

### Mark Booking as In-Progress

```
POST /bookings/:id/start
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Booking started",
  "data": {
    "status": "in_progress",
    "start_time": "2024-01-15T10:35:00Z"
  }
}
```

### Complete Booking

```
POST /bookings/:id/complete
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "final_amount": 176.30,
  "payment_method": "Cash"
}

Response (200):
{
  "success": true,
  "message": "Booking completed",
  "data": {
    "status": "completed",
    "end_time": "2024-01-15T10:50:00Z",
    "total_amount": 176.30
  }
}
```

---

## 💳 Payment Endpoints

### Get Payment Methods

```
GET /payments/methods
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Payment methods retrieved",
  "data": [
    {
      "id": 1,
      "type": "Credit Card",
      "last_four": "1234",
      "brand": "Visa",
      "is_default": true
    },
    {
      "id": 2,
      "type": "UPI",
      "upi_id": "user@bank",
      "is_default": false
    }
  ]
}
```

### Process Payment

```
POST /payments/process
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "booking_id": 1,
  "payment_method": "Credit Card",
  "payment_method_id": 1,
  "amount": 176.30,
  "currency": "INR"
}

Response (200):
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "id": 1,
    "payment_reference_id": "PAY-001",
    "booking_id": 1,
    "amount": 176.30,
    "status": "completed",
    "transaction_id": "TXN-123456",
    "processed_at": "2024-01-15T10:50:00Z"
  }
}
```

### Get Payment History

```
GET /payments/history?page=1&per_page=10
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Payment history retrieved",
  "data": [
    {
      "id": 1,
      "payment_reference_id": "PAY-001",
      "booking_id": 1,
      "amount": 176.30,
      "payment_method": "Credit Card",
      "status": "completed",
      "processed_at": "2024-01-15T10:50:00Z"
    }
  ],
  "pagination": { ... }
}
```

---

## ⭐ Rating & Review Endpoints

### Get Booking Rating (Check if Already Rated)

```
GET /bookings/:id/rating
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Rating retrieved",
  "data": {
    "id": 1,
    "booking_id": 1,
    "rating": 5,
    "review_text": "Great ride!",
    "cleanliness_rating": 5,
    "behavior_rating": 5,
    "safety_rating": 5,
    "created_at": "2024-01-15T10:50:00Z"
  }
}
```

### Submit Booking Rating

```
POST /bookings/:id/rating
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "rating": 5,
  "review_text": "Excellent driver and clean car!",
  "cleanliness_rating": 5,
  "behavior_rating": 5,
  "safety_rating": 5
}

Response (201):
{
  "success": true,
  "message": "Rating submitted successfully",
  "data": {
    "id": 1,
    "booking_id": 1,
    "rating": 5
  }
}
```

---

## 🔔 Notification Endpoints

### Get Notifications

```
GET /notifications?is_read=false&page=1&per_page=20
Authorization: Bearer {access_token}

Query Parameters:
- is_read: boolean
- page: integer
- per_page: integer

Response (200):
{
  "success": true,
  "message": "Notifications retrieved",
  "data": [
    {
      "id": 1,
      "title": "Booking Confirmed",
      "message": "Your booking BK-001 has been confirmed",
      "notification_type": "Booking",
      "is_read": false,
      "action_url": "/bookings/1",
      "created_at": "2024-01-15T10:30:00Z"
    }
  ],
  "unread_count": 3
}
```

### Mark Notification as Read

```
PUT /notifications/:id/read
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Notification marked as read"
}
```

### Mark All Notifications as Read

```
PUT /notifications/read-all
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "All notifications marked as read"
}
```

---

## 🎟️ Discount Code Endpoints

### Validate Discount Code

```
GET /discount-codes/validate/:code
Authorization: Bearer {access_token}

Query Parameters:
- booking_amount: decimal (amount of booking)

Response (200):
{
  "success": true,
  "message": "Discount code is valid",
  "data": {
    "code": "WELCOME20",
    "discount_type": "Percentage",
    "discount_value": 20,
    "max_discount_amount": 500,
    "discount_amount": 176.30,
    "final_amount": 176.30
  }
}
```

---

## 🆘 Support Ticket Endpoints

### Create Support Ticket

```
POST /support-tickets
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "title": "Driver didn't arrive on time",
  "description": "Driver was 30 minutes late for pickup",
  "category": "Driver Behavior",
  "priority": "High",
  "booking_id": 1
}

Response (201):
{
  "success": true,
  "message": "Support ticket created",
  "data": {
    "id": 1,
    "ticket_number": "TKT-001",
    "status": "open",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

### List Support Tickets

```
GET /support-tickets?status=open&page=1
Authorization: Bearer {access_token}

Response (200):
{
  "success": true,
  "message": "Support tickets retrieved",
  "data": [
    {
      "id": 1,
      "ticket_number": "TKT-001",
      "title": "Driver didn't arrive on time",
      "category": "Driver Behavior",
      "priority": "High",
      "status": "open",
      "created_at": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": { ... }
}
```

---

## 🛣️ Route Endpoints

### Calculate Route & Pricing

```
POST /routes/calculate
Authorization: Bearer {access_token}
Content-Type: application/json

Request Body:
{
  "pickup_latitude": 28.6139,
  "pickup_longitude": 77.2090,
  "dropoff_latitude": 28.6200,
  "dropoff_longitude": 77.2150,
  "vehicle_type": "Car",
  "booking_type": "One-Way"
}

Response (200):
{
  "success": true,
  "message": "Route calculated",
  "data": {
    "distance_km": 5.2,
    "duration_minutes": 15,
    "base_fare": 100,
    "distance_charge": 52,
    "time_charge": 0,
    "surge_multiplier": 1.0,
    "subtotal": 152,
    "tax_rate": 0.16,
    "tax_amount": 24.32,
    "total_amount": 176.32,
    "route_polyline": "encoded_polyline_string"
  }
}
```

---

## ⚙️ Error Responses

### Standard Error Response

```json
{
  "success": false,
  "message": "Error description",
  "code": "ERROR_CODE",
  "errors": {
    "field_name": ["Error message"]
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Common Error Codes

```
VALIDATION_ERROR - Request validation failed
AUTHENTICATION_FAILED - Authentication failed
UNAUTHORIZED - User not authorized
NOT_FOUND - Resource not found
CONFLICT - Resource conflict
RATE_LIMITED - Too many requests
INTERNAL_ERROR - Server error
PAYMENT_FAILED - Payment processing failed
BOOKING_NOT_FOUND - Booking not found
DRIVER_NOT_AVAILABLE - No driver available
INVALID_DISCOUNT_CODE - Discount code is invalid
```

---

## 📈 Rate Limiting

```
- Unauthenticated: 100 requests per hour per IP
- Authenticated: 1000 requests per hour per user
- Payment endpoints: 50 requests per hour per user
- Location updates: 60 requests per minute per driver

Headers:
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 950
X-RateLimit-Reset: 1642252800
```

---

## 🔐 Security Headers

```
Authorization: Bearer {access_token}
Content-Type: application/json
X-API-Version: v1
X-Request-ID: unique-request-id
```

---

**Last Updated**: 2024
