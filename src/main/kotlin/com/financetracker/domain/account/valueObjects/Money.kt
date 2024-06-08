package com.financetracker.domain.account.valueObjects

data class Money(var value: Double = 0.0, val currency: Currency) {
  fun add(amountToAdd: Money) {
    this.value = amountToAdd.value
  }
}

enum class Currency {
  CAD
}

operator fun Money.plus(other: Money): Money {
  if (this.currency != other.currency) {
    throw IllegalArgumentException("Cannot add amounts with different currencies")
  }
  return Money(this.value + other.value, this.currency)
}

operator fun Money.minus(other: Money): Money {
  if (this.currency != other.currency) {
    throw IllegalArgumentException("Cannot add amounts with different currencies")
  }
  return Money(this.value - other.value, this.currency)
}
