package whitekim.todo.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import whitekim.todo.model.Todo

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
}