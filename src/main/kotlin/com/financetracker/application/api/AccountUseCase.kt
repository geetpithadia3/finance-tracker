package com.financetracker.application.api

import com.financetracker.application.api.dto.CreateAccountRequest
import com.financetracker.domain.account.model.Account

interface AccountUseCase {
  fun createAccount(createAccountRequest: CreateAccountRequest): Account
}
