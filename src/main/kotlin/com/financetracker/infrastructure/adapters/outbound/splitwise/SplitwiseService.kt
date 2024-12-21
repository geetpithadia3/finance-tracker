package com.financetracker.infrastructure.adapters.outbound.splitwise

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.financetracker.infrastructure.adapters.inbound.dto.request.SplitShare
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SplitwiseService {

  @Value("\${app.splitwise.api.key}") lateinit var splitwiseApiKey: String
  @Value("\${app.splitwise.api.url}") lateinit var splitwiseApiUrl: String

  private val client = OkHttpClient()
  private val mapper =
      jacksonObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
          .registerModule(JavaTimeModule())

  fun getExpensesBetween(
      apikey: String,
      datedAfter: LocalDate,
      datedBefore: LocalDate
  ): SplitwiseExpenseList {
    val request =
        Request.Builder()
            .url(
                "${splitwiseApiUrl}/get_expenses?dated_after=${datedAfter.atStartOfDay()}&dated_before=${datedBefore.atStartOfDay().plusDays(1)}")
            .header("Authorization", "Bearer $splitwiseApiKey")
            .build()
    val expensesJson = client.newCall(request).execute().body?.string()
    return mapper.readValue(expensesJson, SplitwiseExpenseList::class.java)
  }

  fun getCurrentUserId(apikey: String): String {
    val request =
        Request.Builder()
            .url("${splitwiseApiUrl}/get_current_user")
            .header("Authorization", "Bearer $splitwiseApiKey")
            .build()
    val userJson = client.newCall(request).execute().body?.string()
    return mapper.readValue(userJson, UserWrapper::class.java).user.id.toString()
  }

  fun getFriends(apikey: String): List<User> {
    val request =
        Request.Builder()
            .url("${splitwiseApiUrl}/get_friends")
            .header("Authorization", "Bearer $splitwiseApiKey")
            .build()
    val userJson = client.newCall(request).execute().body?.string()
    val friends = mapper.readValue(userJson, FriendsWrapper::class.java).friends
    return friends
  }

  fun addExpense(
      apiKey: String,
      cost: Double,
      description: String,
      date: LocalDate,
      splitShares: List<SplitShare>
  ): String {
    val expenseBody =
        mapOf(
            "cost" to cost.toString(),
            "description" to description,
            "date" to date.format(DateTimeFormatter.ISO_DATE_TIME),
            "repeat_interval" to "never",
            "group_id" to 0) +
            splitShares
                .mapIndexed { index, share ->
                  mapOf(
                      "users__${index}__user_id" to share.userId,
                      "users__${index}__paid_share" to share.paidShare.toString(),
                      "users__${index}__owed_share" to share.owedShare.toString())
                }
                .flatMap { it.entries }
                .associate { it.key to it.value }

    val requestBody =
        FormBody.Builder()
            .apply { expenseBody.forEach { (key, value) -> add(key, value.toString()) } }
            .build()

    val request =
        Request.Builder()
            .url("${splitwiseApiUrl}/create_expense")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string() ?: throw RuntimeException("Empty response body")

    if (!response.isSuccessful) {
      throw RuntimeException("Failed to create expense: ${response.code} - $responseBody")
    }

    val expenseResponse = mapper.readValue(responseBody, ExpenseResponse::class.java)
    return expenseResponse.expenses[0].id.toString()
  }
}
