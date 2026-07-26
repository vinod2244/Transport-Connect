# AP Transport Connect - Complete API Documentation

## Base URL

```
Production: https://api.aptransportconnect.com/api/v1
Staging: https://staging-api.aptransportconnect.com/api/v1
Development: http://localhost:8000/api/v1
```

## Authentication

All endpoints (except auth endpoints) require JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

## Response Format

### Success Response
```json
{
  "status": 200,
  "message": "Success message",
  "data": { /* Response data */ },
  "timestamp": "2024-07-26 10:30:45"
}
```

### Error Response
```json
{
  "status": 400,
  "message": "Error message",
  "errors": { /* Field errors */ },
  "timestamp": "2024-07-26 10:30:45"
}
```

### Paginated Response
```json
{
  "status": 200,
  "message": "Success",
  "data": [
    { /* Items */ }
  ],
  "pagination": {
    "total": 100,
    "current_page": 1,
    "page_size": 20,
    "total_pages": 5
  },
  "timestamp": "2024-07-26 10:30:45"
}
```

## Status Codes

- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `204 No Content` - Successful request with no response body
- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Validation error
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error
- `503 Service Unavailable` - Service temporarily down

## API Endpoints

### 1. Health Check

#### Check API Status
```
GET /health
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "status": "healthy",
    "service": "AP Transport Connect API",
    "version": "1.0.0",
    "database": "connected"
  }
}
```

---

### 2. Authentication Endpoints

#### Register User
```
POST /auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Doe",
  "phone": "9876543210",
  "email": "john@example.com",
  "password": "SecurePass123",
  "role": "customer"
}
```

**Response:**
```json
{
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "phone": "9876543210",
    "email": "john@example.com",
    "role": "customer"
  }
}
```

---

#### Send OTP
```
POST /auth/send-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "phone": "9876543210"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "OTP sent successfully",
  "data": {
    "otp_id": "otp_123456",
    "expires_in": 600
  }
}
```

---

#### Verify OTP
```
POST /auth/verify-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "phone": "9876543210",
  "otp": "123456"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "OTP verified successfully",
  "data": {
    "is_new_user": true,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

#### Login
```
POST /auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "identifier": "9876543210",
  "password": "SecurePass123",
  "role": "customer",
  "remember_me": false
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "phone": "9876543210",
      "email": "john@example.com",
      "role": "customer"
    },
    "access_token": "******",
    "refresh_token": "******",
    "expires_in": 3600,
    "refresh_expires_in": 604800
  }
}
```

---

### 3. User Endpoints

#### Get User Profile
```
GET /user/profile
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "id": 1,
    "name": "John Doe",
    "phone": "9876543210",
    "email": "john@example.com",
    "profile_image": "https://api.com/uploads/profile/user_1.jpg",
    "date_of_birth": "1990-05-15",
    "gender": "male",
    "address": "123 Main St",
    "city": "Visakhapatnam",
    "state": "Andhra Pradesh",
    "pincode": "530001"
  }
}
```

---

#### Update User Profile
```
PUT /user/profile
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Doe Updated",
  "email": "john.updated@example.com",
  "date_of_birth": "1990-05-15",
  "gender": "male",
  "address": "456 New St",
  "city": "Vijayawada"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Profile updated successfully",
  "data": { /* Updated user data */ }
}
```

---

### 4. Vehicle Search Endpoints

#### Search Vehicles
```
GET /vehicles/search?pickup_lat=17.6869&pickup_lon=83.2185&dropoff_lat=16.5062&dropoff_lon=80.6480&vehicle_type=mini_truck&date=2024-07-26
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `pickup_lat` (required) - Pickup latitude
- `pickup_lon` (required) - Pickup longitude
- `dropoff_lat` (required) - Dropoff latitude
- `dropoff_lon` (required) - Dropoff longitude
- `vehicle_type` (optional) - Vehicle type filter
- `date` (optional) - Booking date
- `page` (optional) - Page number (default: 1)
- `per_page` (optional) - Results per page (default: 20)

**Response:**
```json
{
  "status": 200,
  "data": [
    {
      "id": 1,
      "registration_number": "AP 16 AB 1234",
      "vehicle_type": "mini_truck",
      "load_capacity": 2,
      "load_capacity_unit": "ton",
      "fuel_type": "diesel",
      "color": "white",
      "owner": {
        "id": 1,
        "name": "Owner Name",
        "rating": 4.5
      },
      "current_driver": {
        "id": 1,
        "name": "Driver Name",
        "phone": "9876543210",
        "rating": 4.8
      },
      "distance_from_pickup": 2.5,
      "eta_minutes": 10,
      "estimated_fare": 500,
      "is_available": true
    }
  ],
  "pagination": {
    "total": 15,
    "current_page": 1,
    "page_size": 20,
    "total_pages": 1
  }
}
```

---

#### Get Vehicle Types
```
GET /vehicles/types
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "types": [
      {
        "id": 1,
        "name": "Mini Truck",
        "capacity_min": 1,
        "capacity_max": 2
      },
      {
        "id": 2,
        "name": "Pickup",
        "capacity_min": 1,
        "capacity_max": 1.5
      }
    ]
  }
}
```

---

### 5. Booking Endpoints

#### Create Booking
```
POST /bookings
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "vehicle_id": 1,
  "pickup_location": "123 Main St, Visakhapatnam",
  "pickup_latitude": 17.6869,
  "pickup_longitude": 83.2185,
  "dropoff_location": "456 New St, Vijayawada",
  "dropoff_latitude": 16.5062,
  "dropoff_longitude": 80.6480,
  "load_type": "Electronics",
  "load_weight": 1.5,
  "scheduled_pickup_time": "2024-07-26 14:00:00",
  "notes": "Handle with care"
}
```

**Response:**
```json
{
  "status": 201,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "booking_number": "TXN123456789",
    "status": "pending",
    "vehicle": { /* Vehicle data */ },
    "estimated_fare": 1200,
    "estimated_distance": 150
  }
}
```

---

#### Get Bookings
```
GET /bookings?status=pending&page=1&per_page=20
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `status` (optional) - Filter by status (pending, accepted, started, completed, cancelled)
- `page` (optional) - Page number
- `per_page` (optional) - Results per page

---

#### Get Booking Details
```
GET /bookings/:id
Authorization: Bearer <jwt_token>
```

---

#### Cancel Booking
```
PUT /bookings/:id/cancel
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "cancellation_reason": "Found alternative transport"
}
```

---

### 6. Payment Endpoints

#### Initiate Payment
```
POST /payments/initiate
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "booking_id": 1,
  "amount": 1200,
  "payment_method": "razorpay",
  "payment_type": "card"
}
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "order_id": "order_123456789",
    "amount": 1200,
    "currency": "INR",
    "timeout": 900,
    "redirect_url": "https://api.com/payments/redirect"
  }
}
```

---

#### Verify Payment
```
POST /payments/verify
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "order_id": "order_123456789",
  "payment_id": "pay_123456789",
  "signature": "signature_hash"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Payment verified successfully",
  "data": {
    "payment_status": "completed",
    "transaction_id": "TXN123456",
    "booking_status": "accepted"
  }
}
```

---

#### Get Payment History
```
GET /payments/history?page=1&per_page=20
Authorization: Bearer <jwt_token>
```

---

### 7. Driver Endpoints

#### Get Driver Dashboard
```
GET /driver/dashboard
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "total_earnings": 15000,
    "today_earnings": 800,
    "completed_trips": 45,
    "rating": 4.8,
    "pending_trips": 2,
    "online_status": true
  }
}
```

---

#### Get Active Trips
```
GET /driver/trips?status=pending&page=1
Authorization: Bearer <jwt_token>
```

---

#### Accept Trip
```
PUT /driver/trips/:id/accept
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "eta_minutes": 10
}
```

---

#### Update Location
```
POST /driver/location
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "trip_id": 1,
  "latitude": 17.6869,
  "longitude": 83.2185,
  "accuracy": 10,
  "speed": 45,
  "direction": 180
}
```

---

### 8. Wallet Endpoints

#### Get Wallet Balance
```
GET /wallet
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "balance": 5000,
    "total_added": 50000,
    "total_spent": 45000,
    "currency": "INR"
  }
}
```

---

#### Add Money to Wallet
```
POST /wallet/add-money
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "amount": 1000,
  "payment_method": "card"
}
```

---

### 9. Chat Endpoints

#### Get Conversations
```
GET /chat/conversations?page=1
Authorization: Bearer <jwt_token>
```

---

#### Send Message
```
POST /chat/messages
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "conversation_id": 1,
  "message_type": "text",
  "content": "Hello, where are you?"
}
```

---

### 10. Rating Endpoints

#### Submit Rating
```
POST /ratings
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "booking_id": 1,
  "rating": 4.5,
  "comment": "Great service!",
  "rating_type": "driver"
}
```

---

### 11. Support Endpoints

#### Create Support Ticket
```
POST /support/tickets
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "subject": "Payment issue",
  "description": "Payment failed but amount debited",
  "category": "payment",
  "priority": "high",
  "booking_id": 1
}
```

---

#### Get Support Tickets
```
GET /support/tickets?status=open
Authorization: Bearer <jwt_token>
```

---

## Rate Limiting

API requests are rate limited to:
- **100 requests per minute** for authenticated users
- **10 requests per minute** for unauthenticated endpoints

When rate limit exceeded, response:
```json
{
  "status": 429,
  "message": "Too Many Requests",
  "retry_after": 60
}
```

## Error Handling

### Common Error Responses

**Invalid Request:**
```json
{
  "status": 400,
  "message": "Invalid request",
  "errors": {
    "phone": "Phone number is required",
    "email": "Email format is invalid"
  }
}
```

**Unauthorized:**
```json
{
  "status": 401,
  "message": "Unauthorized",
  "errors": {
    "auth": "Invalid or expired token"
  }
}
```

**Not Found:**
```json
{
  "status": 404,
  "message": "Resource not found",
  "errors": {
    "booking": "Booking with ID 999 not found"
  }
}
```

## Webhooks

### Payment Webhook
```
POST /webhooks/payment
```

Payment gateway sends webhook on payment completion/failure.

### Booking Status Webhook
```
POST /webhooks/booking
```

Sent when booking status changes.

## Best Practices

1. **Always include JWT token** in Authorization header for protected endpoints
2. **Use pagination** for list endpoints (use per_page parameter)
3. **Handle errors gracefully** with appropriate error handling
4. **Implement request timeout** of 30 seconds
5. **Cache responses** where appropriate (GET requests)
6. **Validate input** on client side before sending
7. **Implement retry logic** for failed requests
8. **Log all API requests** for debugging
9. **Use HTTPS** only for production
10. **Keep token secure** and never expose to frontend code

## Support

For API support, contact: api-support@aptransportconnect.com
