package com.financetracker.infrastructure.adapters.outbound.persistence

import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountAdapter(val accountRepository: AccountRepository) : AccountPersistence {
  override fun save(account: Account): String {
    return accountRepository
        .save(
            AccountEntity().apply {
              id = account.id
              type = account.type
              org = account.org
              balance = account.balance
              user = UserEntity().apply { id = account.user }
            })
        .id
  }

  override fun list(user: User): List<Account> {
    return accountRepository.findByUser(UserEntity().apply { id = user.id!! }).map {
      Account(id = it.id, type = it.type, org = it.org, balance = it.balance, user.id!!)
    }
  }

  override fun findByIdAndUser(id: String, user: User): Account? {
    val accountEntity =
        accountRepository.findByIdAndUser(id, UserEntity().also { it.id = user.id!! })

    return accountEntity?.let {
      Account(
          id = accountEntity.id,
          type = accountEntity.type,
          org = accountEntity.org,
          balance = accountEntity.balance,
          user = accountEntity.user.id!!)
    }
  }

  override fun findByUser(user: User): List<Account> {
    TODO("Not yet implemented")
  }
}
