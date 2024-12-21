package com.financetracker.infrastructure.adapters.outbound.splitwise.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true) data class UserWrapper(val user: User)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(val id: Long, var firstName: String? = null) {
  init {
    firstName = firstName?.substring(0, 1)?.uppercase() + firstName?.substring(1)
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserExpense(val userId: Long, val paidShare: String, val owedShare: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SplitwiseExpense(
    val id: Long,
    val users: List<UserExpense>,
    val date: LocalDateTime,
    val category: SplitwiseCategory,
    val description: String,
    val updatedAt: LocalDateTime
)

@JsonIgnoreProperties(ignoreUnknown = true) data class SplitwiseCategory(val name: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SplitwiseExpenseList(val expenses: List<SplitwiseExpense>)
