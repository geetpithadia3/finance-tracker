package com.financetracker.application

import IncomeSourcePersistence
import com.financetracker.application.ports.input.IncomeSourceManagementUseCase
import com.financetracker.domain.model.IncomeSource
import com.financetracker.domain.model.PayFrequency
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddIncomeSourceRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateIncomeSourceRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class IncomeSourceService(private val incomeSourcePersistence: IncomeSourcePersistence) :
    IncomeSourceManagementUseCase {

  override fun addIncomeSource(request: AddIncomeSourceRequest, user: User): IncomeSource {
    val incomeSource =
        IncomeSource(
            userId = user.id!!,
            payFrequency = PayFrequency.valueOf(request.payFrequency.uppercase()),
            payAmount = request.payAmount,
            nextPayDate = request.nextPayDate)
    return incomeSourcePersistence.save(incomeSource)
  }

  override fun updateIncomeSource(
      id: UUID,
      request: UpdateIncomeSourceRequest,
      user: User
  ): IncomeSource {
    val existingSource =
        incomeSourcePersistence.findByUserId(user.id!!).find { it.id == id }
            ?: throw RuntimeException("Income source not found")

    val updatedSource =
        existingSource.copy(
            payFrequency = PayFrequency.valueOf(request.payFrequency.uppercase()),
            payAmount = request.payAmount,
            nextPayDate = request.nextPayDate)
    return incomeSourcePersistence.update(updatedSource)
  }

  override fun listIncomeSources(user: User): List<IncomeSource> {
    return incomeSourcePersistence.findByUserId(user.id!!).filter { !it.isDeleted }
  }

  override fun removeIncomeSource(id: UUID, user: User) {
    val existingSource =
        incomeSourcePersistence.findByUserId(user.id!!).find { it.id == id }
            ?: throw RuntimeException("Income source not found")

    incomeSourcePersistence.delete(id)
  }

  override fun calculateMonthlyIncome(user: User): Double {
    return listIncomeSources(user).sumOf { source ->
      when (source.payFrequency) {
        PayFrequency.WEEKLY -> source.payAmount * 52 / 12
        PayFrequency.BI_WEEKLY -> source.payAmount * 26 / 12
        PayFrequency.MONTHLY -> source.payAmount
        PayFrequency.SEMI_MONTHLY -> source.payAmount * 2
      }
    }
  }
}
