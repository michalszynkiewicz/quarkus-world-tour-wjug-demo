package com.example;

import com.example.repository.Todo;
import com.example.todos.TodosOuterClass;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Mapper {
    public TodoDto entityToDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.id = todo.id;
        dto.description = todo.description;
        dto.title = todo.title;
        dto.todoState = todo.todoState;
        return dto;
    }

    public Todo dtoToEntity(TodoDto todo) {
        Todo entity = new Todo();

        merge(todo, entity);

        return entity;
    }

    public void merge(TodoDto source, Todo target) {
        target.id = source.id;
        target.description = source.description;
        target.title = source.title;
        target.todoState = source.todoState;
    }

    public Todo grpcToEntity(TodosOuterClass.Todo source) {
        Todo target = new Todo();

        target.id = source.getId();
        target.description = source.getDescription();
        target.title = source.getTitle();
        target.todoState = TodoState.valueOf(source.getTodoState().name());

        if (target.id == 0) {
            target.id = null;
        }

        return target;
    }

    public TodosOuterClass.Todo entityToGrpc(Todo todo) {
        return TodosOuterClass.Todo.newBuilder()
                .setId(todo.id)
                .setTitle(todo.title)
                .setDescription(todo.description)
                .setTodoState(TodosOuterClass.State.valueOf(todo.todoState.name()))
                .build();
    }
}
