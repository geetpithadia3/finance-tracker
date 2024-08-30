package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddPayScheduleRequest

interface PayScheduleManagementUseCase {
  fun addPaySchedule(request: AddPayScheduleRequest, user: User): Long
}
