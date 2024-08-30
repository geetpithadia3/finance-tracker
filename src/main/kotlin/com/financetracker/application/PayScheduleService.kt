package com.financetracker.application

import com.financetracker.application.ports.input.PayScheduleManagementUseCase
import com.financetracker.application.ports.output.PaySchedulePersistence
import com.financetracker.domain.model.PaySchedule
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddPayScheduleRequest
import org.springframework.stereotype.Service

@Service
class PayScheduleService(private val paySchedulePersistence: PaySchedulePersistence) :
    PayScheduleManagementUseCase {
  override fun addPaySchedule(request: AddPayScheduleRequest, user: User): Long {
    return paySchedulePersistence.save(
        PaySchedule(startDate = request.startDate, frequency = request.frequency, user = user.id!!))
  }
}