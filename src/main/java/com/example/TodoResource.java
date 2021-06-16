package com.example;

import com.example.repository.Todo;
import com.example.repository.TodoRepository;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/todos")
@Blocking
public class TodoResource {

    @Inject
    Mapper mapper;

    @Inject
    TodoRepository repository;

    @POST
    public Uni<TodoDto> add(TodoDto dto) {
        dto.todoState = TodoState.NEW;
        return repository.add(mapper.dtoToEntity(dto))
                .map(mapper::entityToDto);
    }

    @GET
    public Multi<TodoDto> getAll() {
        return repository.getAll()
                .map(mapper::entityToDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<TodoDto> update(@PathParam("id") long id, TodoDto dto) {
        return repository.modify(id, todo -> mapper.merge(dto, todo))
                .map(mapper::entityToDto);
    }

}
