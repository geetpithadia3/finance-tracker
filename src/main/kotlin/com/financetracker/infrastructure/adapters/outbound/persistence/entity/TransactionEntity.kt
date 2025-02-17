package com.financetracker.infrastructure.adapters.outbound.persistence.entity

import com.financetracker.domain.model.TransactionType
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "transaction_entity")
class TransactionEntity {

  @GeneratedValue(strategy = GenerationType.UUID) @Id lateinit var id: UUID

  @Enumerated(EnumType.STRING) lateinit var type: TransactionType

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  var category: CategoryEntity? = null

  lateinit var description: String

  var amount: Double = 0.0

  lateinit var occurredOn: LocalDate

  var isDeleted: Boolean = false

  @OneToOne(cascade = [CascadeType.PERSIST]) var linkedTransaction: TransactionEntity? = null

  @ManyToOne @JoinColumn(name = "account_id", nullable = false) lateinit var account: AccountEntity

  @ColumnDefault("false") var refunded: Boolean = false

  @ColumnDefault("0.0") var personalShare: Double = 0.0

  @ColumnDefault("0.0") var owedShare: Double = 0.0

  @ColumnDefault("null") var shareMetadata: String? = null
}
