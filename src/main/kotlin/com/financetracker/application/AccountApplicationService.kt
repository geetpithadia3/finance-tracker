package com.financetracker.application

import com.financetracker.application.dto.request.AddTransactionRequest
import com.financetracker.application.dto.request.CreateAccountRequest
import com.financetracker.application.dto.response.AccountBalanceResponse
import com.financetracker.application.queries.account.AccountListQuery
import com.financetracker.domain.account.model.Category
import com.financetracker.domain.account.model.TransactionType
import com.financetracker.domain.account.projections.AccountView
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.AccountEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.TransactionEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.UserEntity
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.AccountRepository
import com.financetracker.infrastructure.adapters.outbound.persistence.respository.account.TransactionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountApplicationService(
    val accountRepository: AccountRepository,
    val transactionRepository: TransactionRepository
) {

  fun createAccount(request: CreateAccountRequest, user: UserEntity): AccountBalanceResponse {
    val accountId = request.org + "_" + request.type
    val account =
        AccountEntity().apply {
          id = accountId
          type = request.type
          org = request.org
          balance = request.initialBalance
          this.user = user
        }

    accountRepository.save(account)

    return AccountBalanceResponse(accountId, request.initialBalance)
  }

  fun getAccounts(query: AccountListQuery, user: UserEntity): List<AccountView> {
    return accountRepository.findByUser(user).map {
      AccountView(accountId = it.id, org = it.org, type = it.type, balance = it.balance)
    }
  }

  //  fun deleteAccount(accountId: String, user: User) {
  //    val account = accountRepository.findByIdAndUser(accountId, user)
  //    account?.let { accountRepository.delete(it) }
  //  }

  fun deleteTransactions(transactions: List<String>, user: UserEntity) {
    transactions.forEach {
      val transaction = transactionRepository.findById(it).orElse(null)
      if (transaction != null && transaction.account?.user == user) {
        transactionRepository.delete(transaction)
      }
    }
  }

  fun addTransactions(
      request: List<AddTransactionRequest>,
      user: UserEntity
  ): List<AddTransactionRequest> {
    request.mapNotNull {
      val account = accountRepository.findByIdAndUser(it.accountId, user)
      account?.let { acc ->
        val transaction =
            TransactionEntity().apply {
              id = UUID.randomUUID().toString()
              type = TransactionType.valueOf(it.type.uppercase())
              category = Category.valueOf(it.category.uppercase())
              description = it.description
              amount = it.amount
              occurredOn = it.occurredOn
              this.account = acc
            }
        transactionRepository.save(transaction)
        it
      }
    }

    return request
  }

  fun listCategories(): List<String> {
    return Category.entries.map { it.toString() }
  }
}
