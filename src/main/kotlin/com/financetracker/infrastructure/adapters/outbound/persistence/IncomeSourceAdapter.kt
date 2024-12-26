package com.financetracker.infrastructure.adapters.outbound.persistence

import IncomeSourcePersistence
import com.financetracker.domain.model.IncomeSource
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.IncomeSourceEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.toModel
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.IncomeSourceRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class IncomeSourceAdapter(
    private val incomeSourceRepository: IncomeSourceRepository,
    private val userRepository: UserRepository
) : IncomeSourcePersistence {

  override fun save(incomeSource: IncomeSource): IncomeSource {
    val userEntity = userRepository.getReferenceById(incomeSource.userId)
    val entity =
        IncomeSourceEntity().apply {
          user = userEntity
          payFrequency = incomeSource.payFrequency
          payAmount = incomeSource.payAmount
          nextPayDate = incomeSource.nextPayDate
          isDeleted = incomeSource.isDeleted
        }
    return incomeSourceRepository.save(entity).toModel()
  }

  override fun findByUserId(userId: UUID): List<IncomeSource> {
    val userEntity = userRepository.getReferenceById(userId)
    return incomeSourceRepository.findByUserAndIsDeletedFalse(userEntity).map { it.toModel() }
  }

  override fun update(incomeSource: IncomeSource): IncomeSource {
    val entity =
        incomeSourceRepository.findById(incomeSource.id!!).orElseThrow {
          RuntimeException("Income source not found")
        }

    entity.apply {
      payFrequency = incomeSource.payFrequency
      payAmount = incomeSource.payAmount
      nextPayDate = incomeSource.nextPayDate
      isDeleted = incomeSource.isDeleted
    }

    return incomeSourceRepository.save(entity).toModel()
  }

  override fun delete(id: UUID) {
    val entity =
        incomeSourceRepository.findById(id).orElseThrow {
          RuntimeException("Income source not found")
        }

    entity.isDeleted = true
    incomeSourceRepository.save(entity)
  }
}
