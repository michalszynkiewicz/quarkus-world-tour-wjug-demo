package com.example.repository;

import com.example.TodoState;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Todo extends PanacheEntity {
    public String title;
    public String description;
    public TodoState todoState;
}
