package com.aptransportconnect.driver.presentation.earnings

import com.aptransportconnect.driver.MainDispatcherRule
import com.aptransportconnect.driver.domain.model.Earnings
import com.aptransportconnect.driver.domain.repository.EarningsRepository
import com.aptransportconnect.driver.domain.usecase.earnings.GetEarningsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EarningsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadEarnings_calculatesAveragePerTrip() = runTest {
        val repository = object : EarningsRepository {
            override suspend fun getEarnings(): Result<Earnings> = Result.success(Earnings(500.0, 2500.0, 9000.0, 30, 4.8f))
        }
        val viewModel = EarningsViewModel(GetEarningsUseCase(repository))
        advanceUntilIdle()
        val state = viewModel.uiState.value as EarningsUiState.Success
        assertThat(state.averagePerTrip).isEqualTo(300.0)
    }
}
