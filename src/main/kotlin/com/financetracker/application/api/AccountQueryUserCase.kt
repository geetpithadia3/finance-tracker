package com.financetracker.application.api

import com.financetracker.application.api.dto.AccountBalanceRequest
import com.financetracker.application.api.dto.AccountTransactionRequest
import com.financetracker.domain.account.projections.AccountBalanceProjection
import com.financetracker.domain.account.projections.AccountTransactionsProjection

interface AccountQueryUserCase {
  fun queryAccountBalance(request: AccountBalanceRequest): AccountBalanceProjection

  fun queryAccountTransactions(request: AccountTransactionRequest): AccountTransactionsProjection
}
