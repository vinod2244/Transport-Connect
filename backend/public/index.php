<?php

// Enable CORS headers
header('Access-Control-Allow-Origin: ' . env('CORS_ALLOWED_ORIGINS', '*'));
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=utf-8');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Bootstrap application
require __DIR__ . '/../bootstrap.php';

// Get request method and URI
$method = $_SERVER['REQUEST_METHOD'];
$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$uri = str_replace('/api', '', $uri);

// Route mapping
$routes = [
    // Health check
    'GET:/health' => 'Health@check',
    
    // Authentication routes
    'POST:/auth/register' => 'Auth@register',
    'POST:/auth/login' => 'Auth@login',
    'POST:/auth/verify-otp' => 'Auth@verifyOtp',
    'POST:/auth/send-otp' => 'Auth@sendOtp',
    'POST:/auth/refresh' => 'Auth@refreshToken',
    'POST:/auth/logout' => 'Auth@logout',
    'POST:/auth/forgot-password' => 'Auth@forgotPassword',
    'POST:/auth/reset-password' => 'Auth@resetPassword',
    'POST:/auth/google' => 'Auth@googleLogin',
    
    // User routes
    'GET:/user/profile' => 'User@getProfile',
    'PUT:/user/profile' => 'User@updateProfile',
    'GET:/user/bookings' => 'User@getBookings',
    'GET:/user/bookings/:id' => 'User@getBookingDetail',
    'POST:/user/bookings' => 'User@createBooking',
    'PUT:/user/bookings/:id/cancel' => 'User@cancelBooking',
    'GET:/user/wallet' => 'User@getWallet',
    'POST:/user/wallet/add-money' => 'User@addMoney',
    'GET:/user/ratings' => 'User@getRatings',
    'POST:/user/ratings' => 'User@createRating',
    
    // Driver routes
    'GET:/driver/dashboard' => 'Driver@getDashboard',
    'GET:/driver/trips' => 'Driver@getTrips',
    'GET:/driver/trips/:id' => 'Driver@getTripDetail',
    'PUT:/driver/trips/:id/accept' => 'Driver@acceptTrip',
    'PUT:/driver/trips/:id/reject' => 'Driver@rejectTrip',
    'PUT:/driver/trips/:id/start' => 'Driver@startTrip',
    'PUT:/driver/trips/:id/complete' => 'Driver@completeTrip',
    'POST:/driver/location' => 'Driver@updateLocation',
    'GET:/driver/earnings' => 'Driver@getEarnings',
    'GET:/driver/wallet' => 'Driver@getWallet',
    'POST:/driver/withdrawal' => 'Driver@requestWithdrawal',
    'GET:/driver/documents' => 'Driver@getDocuments',
    'POST:/driver/documents' => 'Driver@uploadDocument',
    'GET:/driver/profile' => 'Driver@getProfile',
    'PUT:/driver/profile' => 'Driver@updateProfile',
    
    // Owner routes
    'GET:/owner/dashboard' => 'Owner@getDashboard',
    'GET:/owner/vehicles' => 'Owner@getVehicles',
    'GET:/owner/vehicles/:id' => 'Owner@getVehicleDetail',
    'POST:/owner/vehicles' => 'Owner@createVehicle',
    'PUT:/owner/vehicles/:id' => 'Owner@updateVehicle',
    'DELETE:/owner/vehicles/:id' => 'Owner@deleteVehicle',
    'GET:/owner/bookings' => 'Owner@getBookings',
    'PUT:/owner/bookings/:id/accept' => 'Owner@acceptBooking',
    'PUT:/owner/bookings/:id/reject' => 'Owner@rejectBooking',
    'GET:/owner/revenue' => 'Owner@getRevenue',
    'GET:/owner/drivers' => 'Owner@getDrivers',
    'POST:/owner/drivers/:id/assign' => 'Owner@assignDriver',
    'GET:/owner/profile' => 'Owner@getProfile',
    'PUT:/owner/profile' => 'Owner@updateProfile',
    
    // Vehicle routes
    'GET:/vehicles/search' => 'Vehicle@search',
    'GET:/vehicles/:id' => 'Vehicle@getDetail',
    'GET:/vehicles/nearby' => 'Vehicle@getNearby',
    'GET:/vehicles/types' => 'Vehicle@getTypes',
    
    // Booking routes
    'GET:/bookings/:id' => 'Booking@getDetail',
    'GET:/bookings/:id/tracking' => 'Booking@getTracking',
    'POST:/bookings/:id/cancel' => 'Booking@cancel',
    'POST:/bookings/:id/rating' => 'Booking@addRating',
    
    // Payment routes
    'POST:/payments/initiate' => 'Payment@initiate',
    'POST:/payments/verify' => 'Payment@verify',
    'GET:/payments/history' => 'Payment@getHistory',
    'POST:/payments/razorpay/webhook' => 'Payment@razorpayWebhook',
    'POST:/payments/phonepe/webhook' => 'Payment@phonepePeWebhook',
    
    // Tracking routes
    'GET:/tracking/:booking_id' => 'Tracking@getTracking',
    'POST:/tracking/location' => 'Tracking@updateLocation',
    
    // Chat routes
    'GET:/chat/conversations' => 'Chat@getConversations',
    'GET:/chat/conversations/:id' => 'Chat@getConversation',
    'POST:/chat/messages' => 'Chat@sendMessage',
    'GET:/chat/messages/:conversation_id' => 'Chat@getMessages',
    
    // Notification routes
    'GET:/notifications' => 'Notification@getNotifications',
    'PUT:/notifications/:id/read' => 'Notification@markAsRead',
    'POST:/notifications/subscribe' => 'Notification@subscribeToPush',
    
    // Support routes
    'GET:/support/tickets' => 'Support@getTickets',
    'POST:/support/tickets' => 'Support@createTicket',
    'GET:/support/tickets/:id' => 'Support@getTicketDetail',
    'POST:/support/tickets/:id/reply' => 'Support@addReply',
    'GET:/support/faq' => 'Support@getFAQ',
    
    // Admin routes
    'GET:/admin/dashboard' => 'Admin@getDashboard',
    'GET:/admin/users' => 'Admin@getUsers',
    'GET:/admin/drivers' => 'Admin@getDrivers',
    'GET:/admin/owners' => 'Admin@getOwners',
    'GET:/admin/bookings' => 'Admin@getBookings',
    'GET:/admin/payments' => 'Admin@getPayments',
    'GET:/admin/complaints' => 'Admin@getComplaints',
    'GET:/admin/reports' => 'Admin@getReports',
    'GET:/admin/analytics' => 'Admin@getAnalytics',
];

// Find matching route
$routeKey = "$method:$uri";
$handler = null;

foreach ($routes as $route => $action) {
    if (preg_match("~^" . str_replace([':', '/:id'], ['$method:', '/(?P<id>[0-9]+)'], $route) . "$~", $routeKey, $matches)) {
        $handler = $action;
        break;
    }
}

if (!$handler) {
    http_response_code(404);
    echo json_encode(error_response(404, 'Route not found'));
    exit();
}

echo json_encode(response(200, ['message' => 'API endpoint connected']));
