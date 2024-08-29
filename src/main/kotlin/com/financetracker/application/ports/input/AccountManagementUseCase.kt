package com.financetracker.application.ports.input

import com.financetracker.application.dto.request.CreateAccountRequest
import com.financetracker.application.dto.response.AccountBalanceResponse
import com.financetracker.domain.model.User

interface AccountManagementUseCase {

  fun create(request: CreateAccountRequest, user: User): AccountBalanceResponse

  fun list(user: User): List<AccountBalanceResponse>
}
