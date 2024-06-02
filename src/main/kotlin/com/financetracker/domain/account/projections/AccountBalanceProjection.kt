package com.financetracker.domain.account.projections

import com.financetracker.domain.account.events.AccountCreated
import com.financetracker.domain.account.events.AccountCredited
import com.financetracker.domain.account.events.AccountDebited
import com.financetracker.domain.account.events.AccountEvent
import com.financetracker.domain.account.model.AccountType
import com.financetracker.domain.account.model.Organization

class AccountBalanceProjection {
  lateinit var name: String
  lateinit var type: AccountType
  lateinit var organization: Organization
  var balance: Double = 0.0

  companion object {
    fun build(events: List<AccountEvent>): AccountBalanceProjection {
      val projection = AccountBalanceProjection()
      events.forEach { event ->
        when (event) {
          is AccountCreated ->
              projection.apply {
                name = event.name
                type = AccountType.valueOf(event.type)
                organization = Organization.valueOf(event.organization)
              }
          is AccountCredited -> projection.apply { balance += event.amount }
          is AccountDebited -> projection.apply { balance -= event.amount }
        }
      }
      return projection
    }
  }
}
