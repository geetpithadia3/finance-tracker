package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
@Table(name = "recurring_transactions")
class RecurringTransactionEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID()

  @Column(nullable = false) var description: String = ""

  @Column(nullable = false) var amount: Double = 0.0

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  lateinit var category: CategoryEntity

  @ManyToOne @JoinColumn(name = "account_id", nullable = false) lateinit var account: AccountEntity

  @Enumerated(EnumType.STRING) @Column(nullable = false) lateinit var frequency: RecurrenceFrequency

  @Column(name = "start_date", nullable = false) lateinit var startDate: LocalDate

  @Column(name = "end_date") var endDate: LocalDate? = null

  @Enumerated(EnumType.STRING)
  @Column(name = "date_flexibility", nullable = false)
  var dateFlexibility: DateFlexibility = DateFlexibility.EXACT

  @Column(name = "range_start") var rangeStart: Int? = null

  @Column(name = "range_end") var rangeEnd: Int? = null

  @Column var preference: String? = null

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var priority: TransactionPriority = TransactionPriority.MEDIUM

  @Column(name = "is_active", nullable = false) var isActive: Boolean = true

  @Column(name = "last_matched_transaction_id") var lastMatchedTransactionId: UUID? = null

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: LocalDateTime = LocalDateTime.now()

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  var updatedAt: LocalDateTime = LocalDateTime.now()

  @Column(name = "is_variable_amount", nullable = false, columnDefinition = "boolean default false")
  var isVariableAmount: Boolean = false

  @Column(name = "estimated_min_amount") var estimatedMinAmount: Double? = null

  @Column(name = "estimated_max_amount") var estimatedMaxAmount: Double? = null
}

enum class RecurrenceFrequency {
  DAILY,
  WEEKLY,
  BIWEEKLY,
  FOUR_WEEKLY,
  MONTHLY,
  YEARLY
}
