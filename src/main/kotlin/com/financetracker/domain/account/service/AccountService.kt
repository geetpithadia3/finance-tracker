package com.financetracker.domain.account.service

import AccountRepository
import com.financetracker.application.api.dto.CreateAccountRequest
import com.financetracker.domain.account.AccountNotFoundException
import com.financetracker.domain.account.model.Account
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountService(private val accountRepository: AccountRepository) {
  fun createAccount(createAccountRequest: CreateAccountRequest): Account {
    val account = Account.create(createAccountRequest)
    val createdAccount = accountRepository.save(account)
    return createdAccount
  }

  fun validateAccount(accountId: UUID) {
    val account = accountRepository.findById(accountId)
    if (account.isEmpty) {
      throw AccountNotFoundException("Account not found")
    }
  }

  fun findAllAccountId(): List<UUID> {
    return accountRepository.findAll()
  }
}
