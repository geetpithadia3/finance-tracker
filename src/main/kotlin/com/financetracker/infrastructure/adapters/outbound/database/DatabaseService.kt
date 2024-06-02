package com.financetracker.infrastructure.adapters.outbound.database

import AccountRepository
import com.financetracker.domain.account.model.Account
import com.financetracker.domain.account.model.AccountType
import com.financetracker.domain.account.model.Organization
import com.financetracker.infrastructure.adapters.outbound.database.entities.AccountEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class DatabaseService(private val accountJPARepository: AccountJPARepository) : AccountRepository {
  override fun save(account: Account): Account {
    val accountEntity =
        AccountEntity(
            id = UUID.randomUUID(),
            name = account.name,
            type = account.type.toString(),
            description = account.description,
            organization = account.organization.toString())
    val createdAccountEntity = accountJPARepository.save(accountEntity)
    return Account().apply {
      id = createdAccountEntity.id
      name = createdAccountEntity.name
      type = AccountType.valueOf(createdAccountEntity.type)
      description = createdAccountEntity.description
      organization = Organization.valueOf(createdAccountEntity.organization)
    }
  }

  override fun findById(id: UUID): Optional<Account> {
    val accountEntity = accountJPARepository.findById(id)
    return if (accountEntity.isPresent) {
      Optional.of(
          Account().apply {
            this.id = accountEntity.get().id
            name = accountEntity.get().name
            type = AccountType.valueOf(accountEntity.get().type)
            description = accountEntity.get().description
            organization = Organization.valueOf(accountEntity.get().organization)
          })
    } else Optional.empty()
  }

  override fun findAll(): List<UUID> {
    val accountEntities = accountJPARepository.findAll()
    return accountEntities.map { it.id }
  }
}
