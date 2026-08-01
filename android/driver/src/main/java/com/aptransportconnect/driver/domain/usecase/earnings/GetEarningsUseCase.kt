package com.aptransportconnect.driver.domain.usecase.earnings

import com.aptransportconnect.driver.domain.model.Earnings
import com.aptransportconnect.driver.domain.repository.EarningsRepository
import javax.inject.Inject

class GetEarningsUseCase @Inject constructor(
    private val earningsRepository: EarningsRepository
) {
    suspend operator fun invoke(): Result<Earnings> = earningsRepository.getEarnings()
}
