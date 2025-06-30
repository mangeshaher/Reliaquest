package com.reliaquest.api.controller;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class IEmployeeControllerImpl implements IEmployeeController<Employee, EmployeeRequest>{

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployeesWithErrorHandlingResponse();
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return employeeService.searchEmployeesByNameLocallyResponse(searchString);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return employeeService.getEmployeeByIdWithErrorHandling(id);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return employeeService.getHighestSalaryOfEmployees();
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return employeeService.getTopTenHighestEarningEmployeeNames();
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeRequest employeeInput) {
        return employeeService.createEmployee(employeeInput);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return employeeService.deleteEmployeeById(id);
    }
}
