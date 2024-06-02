package com.financetracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.integration.annotation.IntegrationComponentScan

@SpringBootApplication
@IntegrationComponentScan("com.financetracker")
class FinanceTrackerApplication

fun main(args: Array<String>) {
    runApplication<FinanceTrackerApplication>(*args)
}
