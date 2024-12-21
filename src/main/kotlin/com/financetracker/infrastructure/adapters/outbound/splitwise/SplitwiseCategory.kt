enum class SplitwiseCategory(val value: String) {
  UTILITIES("Utilities"),
  ELECTRICITY("Electricity"),
  HEAT_GAS("Heat/gas"),
  WATER("Water"),
  TRASH("Trash"),
  TV_PHONE_INTERNET("TV/Phone/Internet"),
  CLEANING("Cleaning"),
  HOME("Home"),
  FURNITURE("Furniture"),
  HOUSEHOLD_SUPPLIES("Household supplies"),
  MAINTENANCE("Maintenance"),
  MORTGAGE("Mortgage"),
  RENT("Rent"),
  ENTERTAINMENT("Entertainment"),
  GAMES("Games"),
  MOVIES("Movies"),
  MUSIC("Music"),
  SPORTS("Sports"),
  FOOD_AND_DRINK("Food and drink"),
  DINING_OUT("Dining out"),
  GROCERIES("Groceries"),
  LIQUOR("Liquor"),
  TRANSPORTATION("Transportation"),
  BICYCLE("Bicycle"),
  BUS_TRAIN("Bus/train"),
  CAR("Car"),
  GAS_FUEL("Gas/fuel"),
  HOTEL("Hotel"),
  PARKING("Parking"),
  PLANE("Plane"),
  TAXI("Taxi"),
  LIFE("Life"),
  CHILDCARE("Childcare"),
  CLOTHING("Clothing"),
  EDUCATION("Education"),
  GIFTS("Gifts"),
  INSURANCE("Insurance"),
  MEDICAL_EXPENSES("Medical expenses"),
  TAXES("Taxes"),
  UNCATEGORIZED("Uncategorized"),
  GENERAL("General"),
  OTHER("Other"),
  PETS("Pets"),
  SERVICES("Services")
}

// fun SplitwiseCategory.toCategory(): Category {
//  return when (this) {
//    SplitwiseCategory.UTILITIES,
//    SplitwiseCategory.ELECTRICITY,
//    SplitwiseCategory.HEAT_GAS,
//    SplitwiseCategory.TRASH,
//    SplitwiseCategory.TV_PHONE_INTERNET,
//    SplitwiseCategory.WATER -> Category.UTILITIES
//    SplitwiseCategory.CLEANING,
//    SplitwiseCategory.HOUSEHOLD_SUPPLIES -> Category.HOUSEHOLD
//    SplitwiseCategory.ENTERTAINMENT,
//    SplitwiseCategory.GAMES,
//    SplitwiseCategory.MOVIES,
//    SplitwiseCategory.MUSIC -> Category.ENTERTAINMENT
//    SplitwiseCategory.SPORTS -> Category.SPORTS
//    SplitwiseCategory.FOOD_AND_DRINK,
//    SplitwiseCategory.DINING_OUT -> Category.DINING_OUT
//    SplitwiseCategory.GROCERIES -> Category.GROCERY
//    SplitwiseCategory.LIQUOR -> Category.DRINKS
//    SplitwiseCategory.HOME,
//    SplitwiseCategory.FURNITURE,
//    SplitwiseCategory.MAINTENANCE,
//    SplitwiseCategory.MORTGAGE,
//    SplitwiseCategory.RENT -> Category.HOUSEHOLD
//    SplitwiseCategory.PETS -> Category.PERSONAL_CARE
//    SplitwiseCategory.SERVICES -> Category.GENERAL
//    SplitwiseCategory.TRANSPORTATION,
//    SplitwiseCategory.BICYCLE,
//    SplitwiseCategory.BUS_TRAIN,
//    SplitwiseCategory.PARKING,
//    SplitwiseCategory.PLANE,
//    SplitwiseCategory.TAXI -> Category.TRANSIT
//    SplitwiseCategory.CAR,
//    SplitwiseCategory.GAS_FUEL -> Category.CAR
//    SplitwiseCategory.HOTEL -> Category.TRAVEL
//    SplitwiseCategory.CHILDCARE,
//    SplitwiseCategory.EDUCATION -> Category.EDUCATION
//    SplitwiseCategory.CLOTHING -> Category.PERSONAL_CARE
//    SplitwiseCategory.GIFTS -> Category.GIFTS
//    SplitwiseCategory.INSURANCE,
//    SplitwiseCategory.MEDICAL_EXPENSES -> Category.HEALTHCARE
//    SplitwiseCategory.TAXES -> Category.GENERAL
//    SplitwiseCategory.UNCATEGORIZED,
//    SplitwiseCategory.GENERAL,
//    SplitwiseCategory.OTHER -> Category.GENERAL
//    else -> Category.GENERAL
//  }
// }
//
// fun Category.toSplitwiseCategory(): SplitwiseCategory {
//  return when (this) {
//    Category.UTILITIES -> SplitwiseCategory.UTILITIES
//    Category.HOUSEHOLD -> SplitwiseCategory.HOUSEHOLD_SUPPLIES
//    Category.ENTERTAINMENT -> SplitwiseCategory.ENTERTAINMENT
//    Category.SPORTS -> SplitwiseCategory.SPORTS
//    Category.DINING_OUT -> SplitwiseCategory.DINING_OUT
//    Category.GROCERY -> SplitwiseCategory.GROCERIES
//    Category.DRINKS -> SplitwiseCategory.LIQUOR
//    Category.TRANSIT -> SplitwiseCategory.TRANSPORTATION
//    Category.CAR -> SplitwiseCategory.CAR
//    Category.TRAVEL -> SplitwiseCategory.HOTEL
//    Category.EDUCATION -> SplitwiseCategory.EDUCATION
//    Category.PERSONAL_CARE -> SplitwiseCategory.CLOTHING
//    Category.GIFTS -> SplitwiseCategory.GIFTS
//    Category.HEALTHCARE -> SplitwiseCategory.MEDICAL_EXPENSES
//    Category.GENERAL -> SplitwiseCategory.GENERAL
//    Category.SAVINGS -> SplitwiseCategory.GENERAL
//    Category.TRANSFER -> SplitwiseCategory.GENERAL
//    Category.INCOME -> SplitwiseCategory.GENERAL
//    Category.SUBSCRIPTION -> SplitwiseCategory.GENERAL
//    Category.INVESTMENT -> SplitwiseCategory.GENERAL
//  }
// }
