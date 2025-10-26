package whitekim.todo.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import whitekim.todo.model.Todo
import whitekim.todo.repository.TodoRepository

@Service
@Transactional
class TodoService (var todoRepository: TodoRepository) {

    fun saveTodo(todo: Todo) {
        todoRepository.save<Todo>(todo);
    }

    fun getTodo(id : Long) : Todo {
        return todoRepository.findById(id).orElseThrow();
    }

    fun getTodoList() : List<Todo> {
        return todoRepository.findAll();
    }

    fun deleteTodoById(id : Long) {
        todoRepository.deleteById(id);
    }
}