package com.financetracker.infrastructure.adapters.outbound.splitwise.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true) data class FriendsWrapper(val friends: List<User>)
