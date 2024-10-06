package com.financetracker.domain.model

enum class TransactionType {
  DEBIT,
  CREDIT;

  companion object {
    fun fromString(type: String): TransactionType {
      return when (type.lowercase()) {
        "debit" -> DEBIT
        "credit" -> CREDIT
        "expense" -> DEBIT
        "income" -> CREDIT
        else -> throw IllegalArgumentException("Unknown transaction type: $type")
      }
    }
  }
}
