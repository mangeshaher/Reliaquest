package com.reliaquest.api.models;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;

    private Integer salary;

    private Integer age;

    private String title;

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", age=" + age +
                ", title='" + title + '\'' +
                '}';
    }
}