package com.financetracker.infrastructure.adapters.outbound.splitwise

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.financetracker.application.ports.output.SharingService
import com.financetracker.domain.model.SharingTransaction
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.SplitwiseExpenseList
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import org.slf4j.LoggerFactory

@Service
class SplitwiseSharingService(@Value("\${app.splitwise.api.url}") var splitwiseApiUrl: String) :
    SharingService {
  private val logger = LoggerFactory.getLogger(SplitwiseSharingService::class.java)

  override fun getTransactionsForUser(
      userId: String,
      userKey: String,
      lastSyncTime: LocalDateTime
  ): List<SharingTransaction> {

    logger.info("Fetching Splitwise transactions for user: $userId, since: $lastSyncTime")
    
    val transactions = getExpensesDatedAfter(lastSyncTime, userKey)

    val filteredTransactions = transactions.expenses.flatMap { expense ->
      expense.users
          .filter { it.userId == userId.toLong() }
          .map {
            SharingTransaction(
                id = expense.id.toString(),
                amount = it.owedShare.toDouble(),
                occurredOn = expense.date.toLocalDate(),
                description = expense.description,
                category = expense.category.toString(),
                type = "EXPENSE",
                updatedAt = expense.updatedAt)
          }
    }

    logger.info("Fetched ${filteredTransactions.size} Splitwise transactions for user: $userId")
    return filteredTransactions
  }

  private fun getExpensesDatedAfter(
      datedAfter: LocalDateTime,
      userKey: String
  ): SplitwiseExpenseList {
    logger.info("Calling Splitwise API to get expenses dated after: $datedAfter")
    val client = OkHttpClient()
    val request =
        Request.Builder()
            .url("${splitwiseApiUrl}/get_expenses?dated_after=${datedAfter}")
            .header("Authorization", "Bearer $userKey")
            .build()
    val expensesJson = client.newCall(request).execute().body?.string()
    val mapper =
        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    mapper.registerModules(JavaTimeModule())
    val expenseList = mapper.readValue(expensesJson, SplitwiseExpenseList::class.java)
    logger.info("Received ${expenseList.expenses.size} expenses from Splitwise API")
    return expenseList
  }
}
