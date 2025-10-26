package whitekim.todo.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import whitekim.todo.model.Todo
import whitekim.todo.service.TodoService

@RestController
@RequestMapping("/todo")
class TodoController(var todoService: TodoService) {
    @PostMapping("/save")
    fun save(todo : Todo) {
        todoService.saveTodo(todo);
    }

    @GetMapping("/list")
    fun getList() : List<Todo> {
        return todoService.getTodoList();
    }

    @GetMapping("/detail")
    fun getTodo(@RequestParam id : Long) : Todo {
        return todoService.getTodo(id);
    }

    @DeleteMapping("/delete")
    fun deleteById(@RequestParam id : Long) {
        return todoService.deleteTodoById(id);
    }
}