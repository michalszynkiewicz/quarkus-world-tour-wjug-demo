package com.example.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
public class TodoRepository {

    @Inject
    Event<Todo> todoUpdate;

    public Uni<Todo> add(Todo todo) {
        return Panache.withTransaction(
                () -> Todo.persist(todo)
                        .replaceWith(todo)
                        .invoke(todoUpdate::fireAsync)
        );
    }

    public Multi<Todo> getAll() {
        return Todo.streamAll();
    }

    public Uni<Todo> modify(long id, Consumer<Todo> modification) {
        return Panache.withTransaction(
                () -> {
                    Uni<Todo> todo = Todo.findById(id);
                    return todo
                            .invoke(modification)
                            .invoke(todoUpdate::fireAsync);
                }
        );

    }
}
