package com.financetracker.application

import com.financetracker.application.queries.TransactionsForMonthQuery
import com.financetracker.domain.account.projections.MonthTransactionsView
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service

@Service
class ExpensesApplicationService(val queryGateway: QueryGateway) {
  fun getExpenses(query: TransactionsForMonthQuery): List<MonthTransactionsView> {
    return queryGateway
        .query(query, ResponseTypes.multipleInstancesOf(MonthTransactionsView::class.java))
        .get()
  }
}
