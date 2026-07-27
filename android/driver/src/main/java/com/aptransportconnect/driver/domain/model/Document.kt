package com.aptransportconnect.driver.domain.model

data class Document(
    val id: String,
    val type: DocumentType,
    val name: String,
    val status: DocumentStatus,
    val expiryDate: String?,
    val uploadedAt: Long?,
    val url: String?
)

enum class DocumentType { LICENSE, VEHICLE_REGISTRATION, INSURANCE, PUC, PERMIT, PROFILE_PHOTO }
enum class DocumentStatus { PENDING, VERIFIED, REJECTED, EXPIRED }
