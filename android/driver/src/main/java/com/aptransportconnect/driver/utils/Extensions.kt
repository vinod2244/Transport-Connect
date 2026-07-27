package com.aptransportconnect.driver.utils

import com.aptransportconnect.driver.domain.model.TripStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.toCurrency(): String = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(this)

fun Long.toDisplayDate(pattern: String = "dd MMM yyyy, hh:mm a"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))

fun Long.toDateGroup(): String = toDisplayDate("dd MMM yyyy")

fun TripStatus.displayName(): String = when (this) {
    TripStatus.PENDING -> "Pending"
    TripStatus.ACCEPTED -> "Accepted"
    TripStatus.EN_ROUTE_PICKUP -> "En route to pickup"
    TripStatus.ARRIVED -> "Arrived"
    TripStatus.TRIP_STARTED -> "Trip started"
    TripStatus.COMPLETED -> "Completed"
    TripStatus.CANCELLED -> "Cancelled"
    TripStatus.REJECTED -> "Rejected"
}
