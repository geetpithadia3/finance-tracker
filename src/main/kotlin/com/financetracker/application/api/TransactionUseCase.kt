package com.financetracker.application.api

import com.financetracker.application.api.dto.CreditTransactionRequest
import com.financetracker.application.api.dto.DebitTransactionRequest

interface TransactionUseCase {
  fun credit(creditTransactionRequest: CreditTransactionRequest)

  fun debit(debitTransactionRequest: DebitTransactionRequest)
}
