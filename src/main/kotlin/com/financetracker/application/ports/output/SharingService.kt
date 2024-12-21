package com.financetracker.application.ports.output

import com.financetracker.domain.model.SharingTransaction
import com.financetracker.infrastructure.adapters.inbound.dto.request.SplitShare
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.User
import java.time.LocalDate
import java.time.LocalDateTime

interface SharingService {

  fun getTransactionsForUser(
      userId: String,
      userKey: String,
      lastSyncTime: LocalDateTime
  ): List<SharingTransaction>

  fun getFriends(userKey: String): List<User>

  fun addExpense(
      apiKey: String,
      cost: Double,
      description: String,
      date: LocalDate,
      splitShares: List<SplitShare>
  ): String
}
