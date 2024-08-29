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

@Service
class SplitwiseSharingService(@Value("\${app.splitwise.api.url}") var splitwiseApiUrl: String) :
    SharingService {
  override fun getTransactionsForUser(
      userId: String,
      userKey: String,
      lastSyncTime: LocalDateTime
  ): List<SharingTransaction> {

    val transactions = getExpensesDatedAfter(lastSyncTime, userKey)

    return transactions.expenses.flatMap { expense ->
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
  }

  private fun getExpensesDatedAfter(
      datedAfter: LocalDateTime,
      userKey: String
  ): SplitwiseExpenseList {
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
    return mapper.readValue(expensesJson, SplitwiseExpenseList::class.java)
  }
}
