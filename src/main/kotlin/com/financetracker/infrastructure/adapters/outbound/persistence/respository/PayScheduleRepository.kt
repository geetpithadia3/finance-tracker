package com.financetracker.infrastructure.adapters.outbound.persistence.respository

import com.financetracker.infrastructure.adapters.outbound.persistence.entity.PaySchedule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PayScheduleRepository : JpaRepository<PaySchedule, UUID> {}
