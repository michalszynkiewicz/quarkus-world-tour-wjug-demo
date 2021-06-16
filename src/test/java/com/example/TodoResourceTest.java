package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TodoResourceTest {
    @Test
    public void shouldAddTodo() {
        addTodo("{\"title\": \"dog\", " +
                "\"description\":\"walk the dog\"}");
    }

    @Test
    public void shouldListTodos() {
        Response post = addTodo("{\"title\": \"cat\", \"description\": \"feed the cat\"}");
        long id = post.jsonPath().getLong("id");

        assertState(id, "NEW");
    }

    @Test
    public void shouldMarkTodoDone() {
        Response post = addTodo("{\"title\": \"hamster\", \"description\": \"feed the hamster\"}");
        long id = post.jsonPath().getLong("id");
        assertState(id, "NEW");

        modifyTodo(id, String.format("{\"title\": \"hamster\", \"description\": \"feed the hamster\"," +
                " \"id\": %s, \"todoState\": \"DONE\"}", id))
                .then().statusCode(200);
        assertState(id, "DONE");
    }

    private Response modifyTodo(Long id, String dto) {
        Response post = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .body(dto)
                .when().put("/todos/" + id);
        post.then().statusCode(200);
        return post;
    }

    private void assertState(long id, String expectedState) {
        Response get = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .when().get("/todos");

        get.then().statusCode(200);
        String state = get.jsonPath().getString(String.format("find {it.id == %s}.todoState", id));

        assertThat(state).isEqualTo(expectedState);
    }

    private Response addTodo(String dto) {
        Response post = given()
                .accept(ContentType.JSON).contentType(ContentType.JSON)
                .body(dto)
                .when().post("/todos");
        post.then().statusCode(200);
        return post;
    }
}
