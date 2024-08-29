package com.financetracker.application.ports.output

import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User

interface AccountPersistence {
  fun save(account: Account): String

  fun list(user: User): List<Account>

  fun findByIdAndUser(id: String, user: User): Account?

  fun findByUser(user: User): List<Account>
}
