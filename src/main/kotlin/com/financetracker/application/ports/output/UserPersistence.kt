package com.financetracker.application.ports.output

import com.financetracker.domain.model.User

interface UserPersistence {
  fun save(user: User): Long
}
