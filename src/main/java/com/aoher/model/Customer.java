package com.aoher.model;

import java.time.LocalDateTime;

public class Customer {

    private Long id;
    private String name;
    private Integer age;
    private LocalDateTime createdDate;

    public Customer() {
    }

    public Customer(String name, Integer age) {
        this(null, name, age, null);
    }

    public Customer(Long id, String name, Integer age, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", createdDate=" + createdDate +
                '}';
    }
}
