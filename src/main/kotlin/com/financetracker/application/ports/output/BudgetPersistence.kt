package com.financetracker.application.ports.output

import com.financetracker.domain.model.Budget
import com.financetracker.domain.model.User
import java.time.YearMonth

interface BudgetPersistence {
  fun save(budget: Budget): Budget

  fun findByUserAndYearMonth(user: User, yearMonth: YearMonth): Budget?

  fun findByUser(user: User): List<Budget>

  fun update(budget: Budget): Budget

  fun findLatestBeforeYearMonth(user: User, yearMonth: YearMonth): Budget?
}
