package com.financetracker.infrastructure.adapters.outbound.splitwise

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.financetracker.infrastructure.adapters.outbound.splitwise.dto.ExpenseList
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SplitwiseService {

  @Value("\${app.splitwise.api.key}") lateinit var splitwiseApiKey: String
  @Value("\${app.splitwise.api.url}") lateinit var splitwiseApiUrl: String

  fun getExpensesBetween(datedAfter: LocalDate, datedBefore: LocalDate): ExpenseList {
    val client = OkHttpClient()
    val request =
        Request.Builder()
            .url(
                "${splitwiseApiUrl}/get_expenses?dated_after=${datedAfter.atStartOfDay()}&dated_before=${datedBefore.atStartOfDay().plusDays(1)}")
            .header("Authorization", "Bearer $splitwiseApiKey")
            .build()
    val expensesJson = client.newCall(request).execute().body?.string()
    val mapper =
        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    mapper.registerModules(JavaTimeModule())
    return mapper.readValue(expensesJson, ExpenseList::class.java)
  }
}
