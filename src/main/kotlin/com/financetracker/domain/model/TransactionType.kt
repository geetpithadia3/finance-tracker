package com.financetracker.domain.model

enum class TransactionType(val value: String) {
  DEBIT("Debit"),
  CREDIT("Credit");

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

enum class TransactionSubType {
  SHARED,
  STANDARD
}
