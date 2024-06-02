package com.financetracker.domain.account.projections

import com.financetracker.domain.account.events.AccountDebited
import java.time.LocalDate

data class PeriodTransactionProjection(val startDate: LocalDate, val endDate: LocalDate) {
  var total: Double = 0.0
  val transactions: MutableList<ExpenseProjection> = mutableListOf()

  fun build(
      accountExpenses: List<AccountPeriodTransactionProjection>
  ): PeriodTransactionProjection {
    accountExpenses.forEach {
      this.transactions.addAll(it.transactions)
      this.total += it.total
    }
    return this
  }
}

class ExpenseProjection {
  lateinit var description: String
  var amount: Double = 0.0
  lateinit var category: String
  lateinit var date: LocalDate
  lateinit var account: String

  companion object {
    fun from(account: String, event: AccountDebited): ExpenseProjection {
      return ExpenseProjection().apply {
        amount = event.amount
        category = event.category
        description = event.category
        date = event.occurredOn
        this.account = account
      }
    }
  }
}
