package com.financetracker.infrastructure.adapters.inbound

import com.financetracker.application.GoalsService
import com.financetracker.domain.goal.projection.GoalView
import com.financetracker.infrastructure.adapters.inbound.dto.AddGoalRequest
import com.financetracker.infrastructure.adapters.inbound.dto.PayScheduleRequest
import com.financetracker.infrastructure.adapters.inbound.dto.UpdateGoalRequest
import com.financetracker.infrastructure.adapters.outbound.persistence.entity.PaySchedule
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GoalsController(val goalsService: GoalsService) {

  @PostMapping("/goal")
  fun add(@RequestBody addGoalRequest: AddGoalRequest): ResponseEntity<Unit> {
    return ResponseEntity.ok(goalsService.addGoal(request = addGoalRequest))
  }

  @GetMapping("/goal")
  fun list(): ResponseEntity<List<GoalView>> {
    return ResponseEntity.ok(goalsService.list())
  }

  @PostMapping("/goal/update")
  fun updateProgress(@RequestBody request: UpdateGoalRequest): ResponseEntity<Unit> {
    goalsService.updateGoal(request)
    return ResponseEntity.ok().build()
  }

  @PostMapping("/pay-schedule")
  fun paySchedule(@RequestBody request: PayScheduleRequest): ResponseEntity<PaySchedule> {
    return ResponseEntity.ok(goalsService.addPaySchedule(request))
  }
}
