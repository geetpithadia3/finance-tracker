import com.financetracker.domain.model.IncomeSource
import java.util.*

interface IncomeSourcePersistence {
  fun save(incomeSource: IncomeSource): IncomeSource

  fun findByUserId(userId: UUID): List<IncomeSource>

  fun update(incomeSource: IncomeSource): IncomeSource

  fun delete(id: UUID)
}
