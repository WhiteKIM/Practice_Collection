package whitekim.todo.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Todo (@Id var id : Long, var message : String, var atSchedule : LocalDateTime) {

}