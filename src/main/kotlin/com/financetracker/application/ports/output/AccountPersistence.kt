package com.financetracker.application.ports.output

import com.financetracker.domain.model.Account
import com.financetracker.domain.model.User
import java.util.*

interface AccountPersistence {
  fun save(account: Account): UUID

  fun list(user: User): List<Account>

  fun findByIdAndUser(id: UUID, user: User): Account?

  fun findByUser(user: User): List<Account>
}
