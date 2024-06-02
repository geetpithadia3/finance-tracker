package com.financetracker.application.api.dto

class CreateAccountRequest(
    val name: String,
    val type: String,
    val description: String,
    val organization: String
)