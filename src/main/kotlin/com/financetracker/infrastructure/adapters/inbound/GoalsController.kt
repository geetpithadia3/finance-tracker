package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.GoalService
import com.financetracker.domain.goal.projection.GoalView
import com.financetracker.infrastructure.adapters.inbound.dto.AddGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateGoalRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/goals")
class GoalController(private val goalService: GoalService) {

    @PostMapping
    fun addGoal(@RequestBody request: AddGoalRequest): ResponseEntity<UUID> {
        val goalId = goalService.addGoal(request)
        return ResponseEntity.ok(goalId)
    }

    @PutMapping
    fun updateGoal(@RequestBody request: UpdateGoalRequest): ResponseEntity<Unit> {
        goalService.updateGoal(request)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun list(): ResponseEntity<List<GoalView>> {
        return ResponseEntity.ok(goalService.list())
    }
}