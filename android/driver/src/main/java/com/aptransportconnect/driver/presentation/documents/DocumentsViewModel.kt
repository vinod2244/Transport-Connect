package com.aptransportconnect.driver.presentation.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptransportconnect.driver.domain.model.Document
import com.aptransportconnect.driver.domain.model.DocumentType
import com.aptransportconnect.driver.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DocumentsUiState {
    data object Loading : DocumentsUiState
    data class Success(val documents: List<Document>) : DocumentsUiState
    data class Error(val message: String) : DocumentsUiState
}

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DocumentsUiState>(DocumentsUiState.Loading)
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = DocumentsUiState.Loading
            documentRepository.refreshDocuments()
                .onSuccess { _uiState.value = DocumentsUiState.Success(it) }
                .onFailure { _uiState.value = DocumentsUiState.Error(it.message ?: "Unable to load documents") }
        }
    }

    fun uploadPlaceholder(type: DocumentType) {
        viewModelScope.launch {
            documentRepository.uploadDocument(type, "${type.name.lowercase()}.jpg", ByteArray(0))
            refresh()
        }
    }
}
