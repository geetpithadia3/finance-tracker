package com.financetracker.infrastructure.adapters.outbound.persistence.repository

import com.financetracker.application.ports.output.PaySchedulePersistence
import com.financetracker.domain.model.PaySchedule
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.goal.PayScheduleEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.goal.PayScheduleRepository
import org.springframework.stereotype.Service

@Service
class PayScheduleAdapter(private val payScheduleRepository: PayScheduleRepository) :
    PaySchedulePersistence {
  override fun save(paySchedule: PaySchedule): Long {
    return payScheduleRepository
        .save(
            PayScheduleEntity().apply {
              startDate = paySchedule.startDate
              frequency = paySchedule.frequency
              user = UserEntity().apply { id = paySchedule.user }
            })
        .id!!
  }

  override fun get(user: User): PaySchedule? {
    return payScheduleRepository.findByUser(UserEntity().apply { id = user.id!! }).first().let {
      PaySchedule(startDate = it.startDate, frequency = it.frequency, user = it.user.id)
    }
  }
}
