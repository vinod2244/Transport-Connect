package com.aptransportconnect.driver.domain.repository

import com.aptransportconnect.driver.domain.model.Document
import com.aptransportconnect.driver.domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun observeDocuments(): Flow<List<Document>>
    suspend fun refreshDocuments(): Result<List<Document>>
    suspend fun uploadDocument(type: DocumentType, fileName: String, fileBytes: ByteArray): Result<Unit>
}
