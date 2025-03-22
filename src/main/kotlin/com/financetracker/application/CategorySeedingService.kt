package com.financetracker.application

import com.financetracker.application.ports.output.CategoryPersistence
import com.financetracker.application.ports.output.UserPersistence
import com.financetracker.domain.model.Category
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategorySeedingService(
    private val categoryPersistence: CategoryPersistence,
    private val userPersistence: UserPersistence
) {

  fun seedDefaultCategories(user: UUID) {
    val defaultCategories =
        CategoryName.entries.map { categoryName ->
          Category(name = categoryName.value, isEditable = categoryName.isEditable(), userId = user)
        }

    defaultCategories.forEach { category -> categoryPersistence.save(category) }
  }
}

enum class CategoryName(val value: String) {
  TRANSPORT("Transport"),
  GROCERIES("Groceries"),
  UTILITIES("Utilities"),
  ENTERTAINMENT("Entertainment"),
  HEALTH("Health"),
  SHOPPING("Shopping"),
  DINING("Dining"),
  TRAVEL("Travel"),
  INCOME("Income"),
  SAVINGS("Savings"),
  GENERAL("General"),
  CAR("Car"),
  RESTAURANT("Restaurant"),
  RENT("Rent"),
  PHONE("Phone"),
  TRANSFER("Transfer"),
  CREDIT_CARD_PAYMENT("Credit Card Payment");

  fun isEditable(): Boolean {
    return this != INCOME && this != SAVINGS && this != TRANSFER && this != CREDIT_CARD_PAYMENT
  }
}
