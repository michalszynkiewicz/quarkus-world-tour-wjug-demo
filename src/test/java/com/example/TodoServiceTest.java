package com.example;

import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.awaitility.Awaitility.await;

@QuarkusTest
public class TodoServiceTest {

    @GrpcClient
    Todos client;

    @Test
    public void shouldAddTodo() {
        client.add(TodosOuterClass.Todo.newBuilder()
                .setTitle("laundry")
                .setDescription("pack the washing machine")
                .build())
                .await().atMost(Duration.ofSeconds(5));

        List<String> todoTitles = new CopyOnWriteArrayList<>();
        Multi<TodosOuterClass.Todo> watch = client.watch(TodosOuterClass.Void.getDefaultInstance());
        watch.subscribe().with(todo -> todoTitles.add(todo.getTitle()));

        await().atMost(Duration.ofSeconds(5))
                .until(() -> todoTitles.contains("laundry"));
    }
}
