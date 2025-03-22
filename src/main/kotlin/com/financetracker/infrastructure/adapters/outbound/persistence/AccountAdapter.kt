package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.AccountRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.utils.toModel
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.stereotype.Service

@Service
@Transactional
class AccountAdapter(val accountRepository: AccountRepository) : AccountPersistence {
  override fun save(account: Account): UUID {
    val entity: AccountEntity? =
        if (account.id == null) {
          account.toEntity()
        } else {
          accountRepository.findById(account.id!!).orElse(account.toEntity())
        }
    entity?.balance = account.balance
    return accountRepository.save(entity!!).id
  }

  override fun list(user: User): List<Account> {
    return accountRepository.findByUser(UserEntity().apply { id = user.id!! }).map { it.toModel() }
  }

  override fun findByIdAndUser(id: UUID, user: User): Account? {
    val accountEntity =
        accountRepository.findByIdAndUser(id, UserEntity().also { it.id = user.id!! })

    return accountEntity?.toModel()
  }

  override fun findByUser(user: User): List<Account> {
    val accountEntities = accountRepository.findByUser(UserEntity().also { it.id = user.id!! })

    return accountEntities.map { it.toModel() }
  }

  override fun delete(accountId: UUID, user: User) {
    accountRepository.deleteByIdAndUser(accountId, UserEntity().also { it.id = user.id!! })
  }
}
