package com.financetracker.application

import com.financetracker.application.ports.input.AllocationManagementUseCase
import com.financetracker.application.ports.input.RecurringTransactionManagementUseCase
import com.financetracker.application.ports.output.AccountPersistence
import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.TransactionPersistence
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.model.TransactionType
import com.financetracker.domain.model.User
import com.financetracker.infrastructure.adapters.inbound.dto.response.AllocationResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.PaycheckAllocation
import com.financetracker.infrastructure.adapters.inbound.dto.response.RecurringTransactionResponse
import com.financetracker.infrastructure.adapters.inbound.dto.response.UpcomingExpense
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.RecurrenceFrequency
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import org.springframework.stereotype.Service

@Service
class AllocationService(
    private val transactionPersistence: TransactionPersistence,
    private val accountPersistence: AccountPersistence,
    private val categoryPersistence: CategoryPersistence,
    private val recurringTransactionManagementUseCase: RecurringTransactionManagementUseCase
) : AllocationManagementUseCase {

  override fun getAllocation(yearMonth: YearMonth, user: User): AllocationResponse {
    val accounts = accountPersistence.findByUser(user)

    // Find income category
    val incomeCategory = categoryPersistence.findByNameAndUser(CategoryName.INCOME.value, user)
    val startDate = yearMonth.atDay(1)
    val endDate = yearMonth.atEndOfMonth()

    // Get recurring income transactions
    val recurringIncomeTransactions = recurringTransactionManagementUseCase.findByAccountsAndActive(user)
        .filter { it.category.id == incomeCategory?.id }

    // Get one-time income transactions for this month
    val oneTimeIncomeTransactions = transactionPersistence
        .findByAccountInAndTypeAndIsDeletedAndOccurredOnBetween(
            accounts, TransactionType.CREDIT, false, startDate, endDate)
        .filter { it.category?.id == incomeCategory?.id }
        .filter { transaction ->
            // Filter out transactions that are part of recurring patterns
            recurringIncomeTransactions.none { it.lastMatchedTransactionId == transaction.id }
        }
        .sortedByDescending { it.occurredOn }

    val allPaycheckAllocations = mutableListOf<PaycheckAllocation>()

    // Process recurring income sources
    recurringIncomeTransactions.forEach { recurringIncome ->
      val projectedAllocations = generateProjectedAllocationsFromRecurring(
          recurringIncome,
          user,
          startDate,
          endDate
      )
      allPaycheckAllocations.addAll(projectedAllocations)
    }

    // Process one-time income sources
    oneTimeIncomeTransactions.forEach { transaction ->
      val singleAllocation = generateSingleAllocation(transaction, user)
      allPaycheckAllocations.add(singleAllocation)
    }

    // Sort by date and combine paychecks that occur on the same day
    val combinedAllocations = combinePaychecksOnSameDay(allPaycheckAllocations)

    return AllocationResponse(combinedAllocations)
  }

  private fun generateProjectedAllocationsFromRecurring(
      recurringIncome: RecurringTransactionResponse,
      user: User,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<PaycheckAllocation> {
    val result = mutableListOf<PaycheckAllocation>()

    // Generate all income dates within the target month
    val incomeDates = generateRecurringDates(recurringIncome, startDate, endDate)

    // For each income date, create a paycheck allocation
    incomeDates.forEach { incomeDate ->
      val nextIncomeDate = calculateNextDate(incomeDate, recurringIncome.frequency)

      // Get recurring expenses between this income and the next
      val recurringExpenses = getRecurringExpenses(
          user,
          incomeDate,
          nextIncomeDate
      )

      // Calculate allocation amounts
      val totalAllocationAmount = recurringExpenses.sumOf { it.amount }
      val remainingAmount = recurringIncome.amount - totalAllocationAmount

      result.add(
          PaycheckAllocation(
              id = UUID.randomUUID(),
              amount = recurringIncome.amount,
              date = incomeDate,
              source = recurringIncome.description,
              frequency = recurringIncome.frequency.toString(),
              expenses = recurringExpenses,
              totalAllocationAmount = totalAllocationAmount,
              remainingAmount = remainingAmount,
              nextPaycheckDate = if (nextIncomeDate.isAfter(endDate)) null else nextIncomeDate
          )
      )
    }

    return result
  }

  private fun generateSingleAllocation(
      transaction: Transaction,
      user: User
  ): PaycheckAllocation {
    val date = transaction.occurredOn ?: LocalDate.now()
    val amount = transaction.amount
    val source = transaction.description ?: "Unknown Income"

    // For non-recurring income, we'll look ahead one month for expenses
    val nextDate = date.plusMonths(1)
    val recurringExpenses = getRecurringExpenses(
        user,
        date,
        nextDate
    )

    val totalAllocationAmount = recurringExpenses.sumOf { it.amount }
    val remainingAmount = amount - totalAllocationAmount

    return PaycheckAllocation(
        id = transaction.id ?: UUID.randomUUID(),
        amount = amount,
        date = date,
        source = source,
        frequency = "One-time",
        expenses = recurringExpenses,
        totalAllocationAmount = totalAllocationAmount,
        remainingAmount = remainingAmount,
        nextPaycheckDate = null
    )
  }

  private fun getRecurringExpenses(
      user: User,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<UpcomingExpense> {
    // Get excluded categories (income and transfers only - removing credit card payments)
    val excludedCategories = listOf(
            categoryPersistence.findByNameAndUser(CategoryName.INCOME.value, user),
            categoryPersistence.findByNameAndUser(CategoryName.TRANSFER.value, user)
    ).mapNotNull { it?.id }

    // Get recurring transactions directly from the recurring transaction service
    val recurringExpenseTransactions = recurringTransactionManagementUseCase.findByAccountsAndActive(user)
        .filter { it.category.id !in excludedCategories }

    // Project recurring expenses within the date range
    val expenses = recurringExpenseTransactions.flatMap { recurringTx ->
        // For each recurring transaction, generate dates within our range
        generateRecurringDates(recurringTx, startDate, endDate).map { date ->
            // Create a unique ID based on the recurring transaction ID and the date
            // This ensures we don't get duplicates when expenses appear in multiple paychecks
            val uniqueId = UUID.nameUUIDFromBytes("${recurringTx.id}-${date}".toByteArray())
            
            // Calculate amount based on whether it's variable or not
            val amount = if (recurringTx.isVariableAmount) {
                // For variable amounts, use the average of min and max if available
                // Otherwise fall back to the base amount
                if (recurringTx.estimatedMinAmount != null && recurringTx.estimatedMaxAmount != null) {
                    (recurringTx.estimatedMinAmount + recurringTx.estimatedMaxAmount) / 2
                } else {
                    recurringTx.amount
                }
            } else {
                recurringTx.amount
            }
            
            // Calculate variability factor for UI display
            val variabilityFactor = if (recurringTx.isVariableAmount) {
                if (recurringTx.estimatedMinAmount != null && recurringTx.estimatedMaxAmount != null) {
                    // Calculate a variability factor based on the range
                    val range = recurringTx.estimatedMaxAmount - recurringTx.estimatedMinAmount
                    val midpoint = (recurringTx.estimatedMaxAmount + recurringTx.estimatedMinAmount) / 2
                    if (midpoint > 0) range / midpoint else 0.0
                } else {
                    0.5 // Default variability if we don't have min/max
                }
            } else {
                0.0 // No variability for fixed amounts
            }
            
            UpcomingExpense(
                id = uniqueId,
                description = recurringTx.description,
                amount = amount,
                dueDate = date,
                category = recurringTx.category.name,
                isRecurring = true,
                variabilityFactor = variabilityFactor,
                isVariableAmount = recurringTx.isVariableAmount,
                estimatedMinAmount = recurringTx.estimatedMinAmount,
                estimatedMaxAmount = recurringTx.estimatedMaxAmount
            )
        }
    }.sortedBy { it.dueDate }
    
    // Remove duplicates by using the unique ID
    return expenses.distinctBy { it.id }
  }

  private fun generateRecurringDates(
      recurringTx: RecurringTransactionResponse,
      startDate: LocalDate,
      endDate: LocalDate
  ): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var currentDate = recurringTx.startDate

    // Skip dates before our range
    while (currentDate.isBefore(startDate) &&
           (recurringTx.endDate == null || currentDate.isBefore(recurringTx.endDate))) {
      currentDate = calculateNextDate(currentDate, recurringTx.frequency)
    }

    // Add dates within our range
    while (!currentDate.isAfter(endDate) &&
           (recurringTx.endDate == null || !currentDate.isAfter(recurringTx.endDate))) {
      dates.add(currentDate)
      currentDate = calculateNextDate(currentDate, recurringTx.frequency)
    }

    return dates
  }

  private fun calculateNextDate(currentDate: LocalDate, frequency: RecurrenceFrequency): LocalDate {
    return when (frequency) {
      RecurrenceFrequency.DAILY -> currentDate.plusDays(1)
      RecurrenceFrequency.WEEKLY -> currentDate.plusWeeks(1)
      RecurrenceFrequency.BIWEEKLY -> currentDate.plusWeeks(2)
      RecurrenceFrequency.FOUR_WEEKLY -> currentDate.plusWeeks(4)
      RecurrenceFrequency.MONTHLY -> currentDate.plusMonths(1)
      RecurrenceFrequency.YEARLY -> currentDate.plusYears(1)
    }
  }

  private fun combinePaychecksOnSameDay(allocations: List<PaycheckAllocation>): List<PaycheckAllocation> {
    // Group allocations by date
    val groupedByDate = allocations.groupBy { it.date }
    
    // Create combined allocations
    val combinedAllocations = groupedByDate.map { (date, paychecksOnSameDay) ->
      if (paychecksOnSameDay.size == 1) {
        // If only one paycheck on this day, return it as is
        paychecksOnSameDay.first()
      } else {
        // Combine multiple paychecks on the same day
        val totalAmount = paychecksOnSameDay.sumOf { it.amount }
        val combinedSources = paychecksOnSameDay.joinToString(" + ") { it.source }
        
        // Combine expenses but ensure no duplicates
        val allExpenses = paychecksOnSameDay.flatMap { it.expenses }.distinctBy { it.id }
        val totalAllocationAmount = allExpenses.sumOf { it.amount }
        val remainingAmount = totalAmount - totalAllocationAmount
        
        // Use the earliest next paycheck date among all combined paychecks
        val nextPaycheckDate = paychecksOnSameDay
          .mapNotNull { it.nextPaycheckDate }
          .minOrNull()
        
        PaycheckAllocation(
          id = UUID.randomUUID(),
          amount = totalAmount,
          date = date,
          source = combinedSources,
          frequency = "Combined", // Mark as combined frequency
          expenses = allExpenses,
          totalAllocationAmount = totalAllocationAmount,
          remainingAmount = remainingAmount,
          nextPaycheckDate = nextPaycheckDate
        )
      }
    }.sortedBy { it.date }
    
    // Now properly assign expenses to the correct paychecks
    return assignExpensesToCorrectPaychecks(combinedAllocations)
  }

  private fun assignExpensesToCorrectPaychecks(allocations: List<PaycheckAllocation>): List<PaycheckAllocation> {
    if (allocations.isEmpty()) return emptyList()
    
    // Sort allocations by date
    val sortedAllocations = allocations.sortedBy { it.date }
    
    // Get all expenses across all allocations
    val allExpenses = sortedAllocations.flatMap { it.expenses }.distinctBy { it.id }
    
    // Create a map to hold the updated allocations
    val updatedAllocations = mutableListOf<PaycheckAllocation>()
    
    // For each allocation, determine which expenses should be assigned to it
    for (i in sortedAllocations.indices) {
      val currentAllocation = sortedAllocations[i]
      val currentDate = currentAllocation.date
      val nextPaycheckDate = if (i < sortedAllocations.size - 1) {
        sortedAllocations[i + 1].date
      } else {
        // If this is the last paycheck, look one month ahead
        currentDate.plusMonths(1)
      }
      
      // Get expenses due between this paycheck and the next
      val expensesForThisPaycheck = allExpenses.filter { expense ->
        !expense.dueDate.isBefore(currentDate) && expense.dueDate.isBefore(nextPaycheckDate)
      }
      
      // Calculate new totals
      val totalAllocationAmount = expensesForThisPaycheck.sumOf { it.amount }
      val remainingAmount = currentAllocation.amount - totalAllocationAmount
      
      // Create updated allocation
      updatedAllocations.add(
        PaycheckAllocation(
          id = currentAllocation.id,
          amount = currentAllocation.amount,
          date = currentAllocation.date,
          source = currentAllocation.source,
          frequency = currentAllocation.frequency,
          expenses = expensesForThisPaycheck,
          totalAllocationAmount = totalAllocationAmount,
          remainingAmount = remainingAmount,
          nextPaycheckDate = currentAllocation.nextPaycheckDate
        )
      )
    }
    
    return updatedAllocations
  }
}
