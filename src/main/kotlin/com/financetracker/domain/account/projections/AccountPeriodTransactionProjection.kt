package com.financetracker.domain.account.projections

import com.financetracker.domain.account.events.AccountCreated
import com.financetracker.domain.account.events.AccountDebited
import com.financetracker.domain.account.events.AccountEvent

class AccountPeriodTransactionProjection {
  lateinit var account: String
  var total: Double = 0.0
  val transactions: MutableList<ExpenseProjection> = mutableListOf()

  companion object {
    fun build(events: List<AccountEvent>): AccountPeriodTransactionProjection {
      val projection = AccountPeriodTransactionProjection()
      var account = ""
      events.forEach { event ->
        when (event) {
          is AccountCreated -> {
            account = event.name
            projection.account = event.name
          }
          is AccountDebited -> {
            val expenseProjection = ExpenseProjection.from(account, event)
            projection.transactions.add(expenseProjection)
            projection.total += expenseProjection.amount
          }
        }
      }
      return projection
    }
  }
}
