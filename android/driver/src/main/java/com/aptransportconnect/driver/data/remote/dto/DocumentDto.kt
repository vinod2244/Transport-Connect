package com.aptransportconnect.driver.data.remote.dto

import com.aptransportconnect.driver.domain.model.Document
import com.aptransportconnect.driver.domain.model.DocumentStatus
import com.aptransportconnect.driver.domain.model.DocumentType

data class DocumentDto(
    val id: String,
    val type: String,
    val name: String,
    val status: String,
    val expiryDate: String?,
    val uploadedAt: Long?,
    val url: String?
) {
    fun toDomain() = Document(
        id = id,
        type = runCatching { DocumentType.valueOf(type.uppercase()) }.getOrDefault(DocumentType.LICENSE),
        name = name,
        status = runCatching { DocumentStatus.valueOf(status.uppercase()) }.getOrDefault(DocumentStatus.PENDING),
        expiryDate = expiryDate,
        uploadedAt = uploadedAt,
        url = url
    )
}
