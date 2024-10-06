package com.financetracker.application

import com.financetracker.application.ports.input.AccountManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AccountBalanceResponse
import org.springframework.stereotype.Service

@Service
class AccountService(val accountPersistence: AccountPersistence) : AccountManagementUseCase {
  override fun create(request: CreateAccountRequest, user: User): AccountBalanceResponse {
    val account =
        Account(
            name = request.name,
            type = request.type,
            org = request.org,
            balance = request.initialBalance,
            userId = user.id!!)

    val id = accountPersistence.save(account)

    return AccountBalanceResponse(
        id, request.name, request.org, request.type, request.initialBalance)
  }

  override fun list(user: User): List<AccountBalanceResponse> {
    return accountPersistence.list(user).map {
      AccountBalanceResponse(
          accountId = it.id!!, name = it.name, org = it.org, type = it.type, balance = it.balance)
    }
  }
}
