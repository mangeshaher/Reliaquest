package com.reliaquest.api.models;

import lombok.Data;

import java.util.UUID;

@Data
public class Employee {
    private UUID id;
    private String employeeName;
    private int employeeSalary;
    private int employeeAge;
    private String employeeTitle;
    private String employeeEmail;

    // toString method
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", employeeSalary=" + employeeSalary +
                ", employeeAge=" + employeeAge +
                ", employeeTitle='" + employeeTitle + '\'' +
                ", employeeEmail='" + employeeEmail + '\'' +
                '}';
    }
}