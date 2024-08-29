package com.financetracker.application.ports.output

import com.financetracker.domain.model.SharingTransaction
import java.time.LocalDateTime

interface SharingService {

  fun getTransactionsForUser(
      userId: String,
      userKey: String,
      lastSyncTime: LocalDateTime
  ): List<SharingTransaction>
}
