package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.SyncAccountRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateTransactionRequest

interface TransactionManagementUseCase {

  fun add(requests: List<AddTransactionRequest>, user: User)

  fun update(requests: List<UpdateTransactionRequest>, user: User)

  fun syncWithSplitwise(request: SyncAccountRequest, user: User)
}
