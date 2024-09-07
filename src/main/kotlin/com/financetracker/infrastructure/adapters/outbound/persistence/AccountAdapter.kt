package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountAdapter(val accountRepository: AccountRepository) : AccountPersistence {
  override fun save(account: Account): UUID {
    val entity = accountRepository.findById(account.id!!)
        .orElse(mapToEntity(account))
    entity.balance = account.balance
    return accountRepository.save(entity).id
  }

  override fun list(user: User): List<Account> {
    return accountRepository.findByUser(UserEntity().apply { id = user.id!! }).map {
      mapToDomain(it)
    }
  }

  override fun findByIdAndUser(id: UUID, user: User): Account? {
    val accountEntity =
        accountRepository.findByIdAndUser(id, UserEntity().also { it.id = user.id!! })

    return accountEntity?.let { mapToDomain(it) }
  }

  override fun findByUser(user: User): List<Account> {
    val accountEntities = accountRepository.findByUser(UserEntity().also { it.id = user.id!! })

    return accountEntities.map { mapToDomain(it) }
  }

  private fun mapToDomain(entity: AccountEntity): Account {
    return Account(
        id = entity.id,
        type = entity.type,
        org = entity.org,
        balance = entity.balance,
        name = entity.name,
        userId = entity.user.id)
  }

  private fun mapToEntity(domain: Account): AccountEntity {
    val accountEntity =
        AccountEntity().apply {
          name = domain.name
          type = domain.type
          org = domain.org
          balance = domain.balance
          user = UserEntity().apply { id = domain.userId }
        }
    if (domain.id != null) {
      accountEntity.apply { id = domain.id!! }
    }
    return accountEntity
  }
}
