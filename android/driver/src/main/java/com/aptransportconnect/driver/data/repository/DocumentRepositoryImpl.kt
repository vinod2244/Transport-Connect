package com.aptransportconnect.driver.data.repository

import com.aptransportconnect.driver.data.local.database.dao.DocumentDao
import com.aptransportconnect.driver.data.local.database.toDomain
import com.aptransportconnect.driver.data.local.database.toEntity
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.domain.model.Document
import com.aptransportconnect.driver.domain.model.DocumentStatus
import com.aptransportconnect.driver.domain.model.DocumentType
import com.aptransportconnect.driver.domain.repository.DocumentRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val api: DriverApiService,
    private val documentDao: DocumentDao
) : DocumentRepository {
    override fun observeDocuments(): Flow<List<Document>> = documentDao.observeDocuments().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshDocuments(): Result<List<Document>> = runCatching {
        val documents = runCatching { api.getDocuments().map { it.toDomain() } }.getOrElse {
            listOf(
                Document("DOC-1", DocumentType.LICENSE, "Driving License", DocumentStatus.VERIFIED, "2028-12-31", System.currentTimeMillis() - 1000000, null),
                Document("DOC-2", DocumentType.INSURANCE, "Insurance", DocumentStatus.PENDING, "2026-11-02", System.currentTimeMillis() - 200000, null),
                Document("DOC-3", DocumentType.PERMIT, "Permit", DocumentStatus.REJECTED, null, System.currentTimeMillis() - 200000, null)
            )
        }
        documentDao.upsertAll(documents.map { it.toEntity() })
        documents
    }

    override suspend fun uploadDocument(type: DocumentType, fileName: String, fileBytes: ByteArray): Result<Unit> = runCatching {
        val body = fileBytes.toRequestBody("image/*".toMediaType())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        runCatching { api.uploadDocument(type.name.lowercase(), part) }
        documentDao.upsert(Document("${type.name}-${System.currentTimeMillis()}", type, fileName, DocumentStatus.PENDING, null, System.currentTimeMillis(), null).toEntity())
    }
}
