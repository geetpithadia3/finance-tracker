package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.SyncAccountRequest

interface TransactionManagementUseCase {

  fun add(requests: List<AddTransactionRequest>, user: User)

  fun syncWithSplitwise(request: SyncAccountRequest, user: User)
}
