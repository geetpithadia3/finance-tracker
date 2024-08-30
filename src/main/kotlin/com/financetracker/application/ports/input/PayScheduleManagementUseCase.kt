package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddPayScheduleRequest
import java.util.*

interface PayScheduleManagementUseCase {
  fun addPaySchedule(request: AddPayScheduleRequest, user: User): UUID
}
