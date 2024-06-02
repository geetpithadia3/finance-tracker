package com.financetracker.domain.account.projections

import com.financetracker.domain.account.events.AccountCreated
import com.financetracker.domain.account.events.AccountCredited
import com.financetracker.domain.account.events.AccountDebited
import com.financetracker.domain.account.events.AccountEvent
import com.financetracker.domain.account.model.AccountType
import java.time.LocalDate

class AccountTransactionsProjection {
  lateinit var name: String
  lateinit var type: AccountType
  val transactions: MutableList<TransactionProjection> = mutableListOf()

  companion object {
    fun build(events: List<AccountEvent>): AccountTransactionsProjection {
      val projection = AccountTransactionsProjection()
      events.forEach { event ->
        when (event) {
          is AccountCreated -> {
            projection.name = event.name
            projection.type = AccountType.valueOf(event.type)
          }
          is AccountCredited -> projection.transactions.add(TransactionProjection.from(event))
          is AccountDebited -> projection.transactions.add(TransactionProjection.from(event))
        }
      }
      return projection
    }
  }
}

class TransactionProjection {
  lateinit var description: String
  var amount: Double = 0.0
  lateinit var type: TransactionType
  lateinit var category: String
  lateinit var date: LocalDate

  companion object {
    fun from(event: AccountCredited): TransactionProjection {
      return TransactionProjection().apply {
        amount = event.amount
        category = event.category
        description = event.category
        date = event.occurredOn
        type = TransactionType.CREDIT
      }
    }

    fun from(event: AccountDebited): TransactionProjection {
      return TransactionProjection().apply {
        amount = event.amount
        category = event.category
        description = event.category
        date = event.occurredOn
        type = TransactionType.DEBIT
      }
    }
  }
}

enum class TransactionType {
  CREDIT,
  DEBIT
}
