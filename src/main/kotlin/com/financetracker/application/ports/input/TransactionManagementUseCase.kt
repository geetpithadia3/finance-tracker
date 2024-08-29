package com.financetracker.application.ports.input

import com.financetracker.application.dto.request.AddTransactionRequest
import com.financetracker.application.dto.request.SyncAccountRequest
import com.financetracker.domain.model.User

interface TransactionManagementUseCase {

  fun add(requests: List<AddTransactionRequest>, user: User)

  fun syncWithSplitwise(request: SyncAccountRequest, user: User)
}
