package com.financetracker.application.ports.output

import com.financetracker.domain.model.User
import java.util.*

interface UserPersistence {
  fun save(user: User): UUID
}
