package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.CreateAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.AccountBalanceResponse

interface AccountManagementUseCase {

  fun create(request: CreateAccountRequest, user: User): AccountBalanceResponse

  fun list(user: User): List<AccountBalanceResponse>
}
