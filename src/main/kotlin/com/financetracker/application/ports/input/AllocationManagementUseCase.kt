package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.AllocationResponse
import java.time.YearMonth

interface AllocationManagementUseCase {
    fun getAllocation(yearMonth: YearMonth, user: User): AllocationResponse
}
