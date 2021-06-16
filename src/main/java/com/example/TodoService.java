package com.example;

import com.example.repository.Todo;
import com.example.repository.TodoRepository;
import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@GrpcService
public class TodoService implements Todos {

    public static final TodosOuterClass.Void VOID = TodosOuterClass.Void.getDefaultInstance();
    @Inject
    Mapper mapper;

    @Inject
    TodoRepository repository;

    BroadcastProcessor<Todo> updateBroadcast = BroadcastProcessor.create();

    @Override
    public Uni<TodosOuterClass.Void> add(TodosOuterClass.Todo request) {
        Todo todo = mapper.grpcToEntity(request);
        return repository.add(todo)
                .replaceWith(VOID);
    }

    @Override
    public Uni<TodosOuterClass.Void> markDone(TodosOuterClass.Todo request) {
        return repository.modify(request.getId(), todo -> todo.todoState = TodoState.DONE)
                .replaceWith(VOID);
    }

    @Override
    public Multi<TodosOuterClass.Todo> watch(TodosOuterClass.Void request) {
        return Multi.createBy()
                .concatenating().streams(repository.getAll(), updateBroadcast).map(
                mapper::entityToGrpc
        );
    }

    void watchTodos(@ObservesAsync Todo todo) {
        updateBroadcast.onNext(todo);
    }
}
