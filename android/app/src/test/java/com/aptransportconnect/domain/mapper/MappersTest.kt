package com.aptransportconnect.domain.mapper

import com.aptransportconnect.data.remote.dto.PaymentDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MappersTest {
    @Test
    fun `payment mapper maps success state`() {
        val dto = PaymentDto("BK-1", "SUCCESS", "TXN-1")

        val result = dto.toDomain()

        assertThat(result.bookingId).isEqualTo("BK-1")
        assertThat(result.state.name).isEqualTo("SUCCESS")
        assertThat(result.transactionId).isEqualTo("TXN-1")
    }
}
