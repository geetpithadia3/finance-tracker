import com.financetracker.domain.account.model.Account
import java.util.*

interface AccountRepository {
  fun save(account: Account): Account

  fun findById(id: UUID): Optional<Account>

  fun findAll(): List<UUID>
}
