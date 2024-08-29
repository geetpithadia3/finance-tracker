package com.financetracker.application

import com.financetracker.application.dto.request.CreateAccountRequest
import com.financetracker.application.dto.response.AccountBalanceResponse
import com.financetracker.application.ports.input.AccountManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import org.springframework.stereotype.Service

@Service
class AccountService(val accountPersistence: AccountPersistence) : AccountManagementUseCase {
  override fun create(request: CreateAccountRequest, user: User): AccountBalanceResponse {
    val accountId = request.org + "_" + request.type
    val account =
        Account(
            id = accountId,
            type = request.type,
            org = request.org,
            balance = request.initialBalance,
            user = user.id)

    accountPersistence.save(account)

    return AccountBalanceResponse(accountId, request.initialBalance)
  }

  override fun list(user: User): List<AccountBalanceResponse> {
    return accountPersistence.list(user).map {
      AccountBalanceResponse(accountId = it.id, balance = it.balance)
    }
  }
}
