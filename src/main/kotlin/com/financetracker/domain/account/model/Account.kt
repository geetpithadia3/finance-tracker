package com.financetracker.domain.account.model

import com.financetracker.application.api.dto.CreateAccountRequest
import java.util.*

class Account {
  lateinit var id: UUID
  lateinit var name: String
  lateinit var type: AccountType
  lateinit var description: String
  lateinit var organization: Organization

  companion object {
    fun create(createAccountRequest: CreateAccountRequest): Account {
      val account = Account()
      account.id = UUID.randomUUID()
      account.name = createAccountRequest.name
      account.type = AccountType.valueOf(createAccountRequest.type.uppercase())
      account.description = createAccountRequest.description
      account.organization = Organization.valueOf(createAccountRequest.organization.uppercase())

      return account
    }
  }
}

enum class AccountType {
  CHECKING,
  SAVINGS,
  CREDIT
}

enum class Organization {
  SCOTIA,
  WEALTHSIMPLE
}
