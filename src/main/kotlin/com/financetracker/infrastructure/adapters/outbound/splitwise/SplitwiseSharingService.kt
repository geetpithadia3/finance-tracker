// package com.financetracker.infrastructure.adapters.outbound.splitwise
//
// import SplitwiseCategory
// import com.fasterxml.jackson.databind.PropertyNamingStrategies
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
// import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
// import com.financetracker.application.ports.output.SharingService
// import com.financetracker.domain.model.SharingTransaction
// import com.financetracker.domain.model.TransactionType
// import com.financetracker.infrastructure.adapters.inbound.dto.request.SplitShare
// import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.ExpenseResponse
// import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.FriendsWrapper
// import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.SplitwiseExpenseList
// import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.User
// import okhttp3.FormBody
// import okhttp3.OkHttpClient
// import okhttp3.Request
// import org.slf4j.LoggerFactory
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.stereotype.Service
//// import toCategory
// import java.time.LocalDate
// import java.time.LocalDateTime
//
// @Service
// class SplitwiseSharingService(@Value("\${app.splitwise.api.url}") var splitwiseApiUrl: String) :
//    SharingService {
//  private val logger = LoggerFactory.getLogger(SplitwiseSharingService::class.java)
//
//  override fun getTransactionsForUser(
//      userId: String,
//      userKey: String,
//      lastSyncTime: LocalDateTime
//  ): List<SharingTransaction> {
//
//    logger.info("Fetching Splitwise transactions for user: $userId, since: $lastSyncTime")
//
//    val transactions = getExpensesDatedAfter(lastSyncTime, userKey)
//
//    val filteredTransactions =
//        transactions.expenses.flatMap { expense ->
//          expense.users
//              .filter { it.userId == userId.toLong() }
//              .map {
//                SharingTransaction(
//                    id = expense.id.toString(),
//                    amount = it.owedShare.toDouble(),
//                    occurredOn = expense.date.toLocalDate(),
//                    description = expense.description,
//                    category =
// SplitwiseCategory.valueOf(expense.category.name).toCategory().value,
//                    type = TransactionType.DEBIT.name,
//                    updatedAt = expense.updatedAt)
//              }
//        }
//
//    logger.info("Fetched ${filteredTransactions.size} Splitwise transactions for user: $userId")
//    return filteredTransactions
//  }
//
//  private fun getExpensesDatedAfter(
//      datedAfter: LocalDateTime,
//      userKey: String
//  ): SplitwiseExpenseList {
//    logger.info("Calling Splitwise API to get expenses dated after: $datedAfter")
//    val client = OkHttpClient()
//    val request =
//        Request.Builder()
//            .url("${splitwiseApiUrl}/get_expenses?dated_after=${datedAfter}")
//            .header("Authorization", "Bearer $userKey")
//            .build()
//    val expensesJson = client.newCall(request).execute().body?.string()
//    val mapper =
//        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
//    mapper.registerModules(JavaTimeModule())
//    val expenseList = mapper.readValue(expensesJson, SplitwiseExpenseList::class.java)
//    logger.info("Received ${expenseList.expenses.size} expenses from Splitwise API")
//    return expenseList
//  }
//
//  override fun getFriends(userKey: String): List<User> {
//    val client = OkHttpClient()
//    val request =
//        Request.Builder()
//            .url("${splitwiseApiUrl}/get_friends")
//            .header("Authorization", "Bearer $userKey")
//            .build()
//    val userJson = client.newCall(request).execute().body?.string()
//    val mapper =
//        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
//    val friends = mapper.readValue(userJson, FriendsWrapper::class.java).friends
//    return friends
//  }
//
//  override fun addExpense(
//      apiKey: String,
//      cost: Double,
//      description: String,
//      date: LocalDate,
//      splitShares: List<SplitShare>
//  ): String {
//    val client = OkHttpClient()
//    val expenseBody =
//        mapOf(
//            "cost" to cost.toString(),
//            "description" to description,
//            "date" to date,
//            "repeat_interval" to "never",
//            "group_id" to 0) +
//            splitShares
//                .mapIndexed { index, share ->
//                  mapOf(
//                      "users__${index}__user_id" to share.userId,
//                      "users__${index}__paid_share" to share.paidShare.toString(),
//                      "users__${index}__owed_share" to share.owedShare.toString())
//                }
//                .flatMap { it.entries }
//                .associate { it.key to it.value }
//
//    val requestBody =
//        FormBody.Builder()
//            .apply { expenseBody.forEach { (key, value) -> add(key, value.toString()) } }
//            .build()
//
//    val request =
//        Request.Builder()
//            .url("${splitwiseApiUrl}/create_expense")
//            .header("Authorization", "Bearer $apiKey")
//            .post(requestBody)
//            .build()
//
//    val response = client.newCall(request).execute()
//    val responseBody = response.body?.string() ?: throw RuntimeException("Empty response body")
//
//    if (!response.isSuccessful) {
//      throw RuntimeException("Failed to create expense: ${response.code} - $responseBody")
//    }
//    val mapper =
//        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
//    val expenseResponse = mapper.readValue(responseBody, ExpenseResponse::class.java)
//    return expenseResponse.expenses[0].id.toString()
//  }
// }
