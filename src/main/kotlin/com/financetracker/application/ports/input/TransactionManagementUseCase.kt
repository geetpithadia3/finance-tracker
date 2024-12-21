package com.financetracker.application.ports.input

import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.request.AddTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.ListTransactionsByMonthRequest
import com.financetracker.infrastructure.adapters.inbound.dto.request.UpdateTransactionRequest
import com.financetracker.infrastructure.adapters.inbound.dto.response.TransactionResponse

interface TransactionManagementUseCase {

  fun add(requests: List<AddTransactionRequest>, user: User)

  fun update(requests: List<UpdateTransactionRequest>, user: User)

  fun list(request: ListTransactionsByMonthRequest, user: User): List<TransactionResponse>

  //  fun updateWithShares(transactionRequest: UpdateTransactionSharesRequest, user: User)
  //
  //  fun syncWithSplitwise(request: SyncAccountRequest, user: User)
}
