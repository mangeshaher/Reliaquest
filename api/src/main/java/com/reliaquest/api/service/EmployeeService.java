package com.reliaquest.api.service;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    // URL of the external API that provides employee data
    private static final String EMPLOYEE_API_URL = "http://localhost:8112/api/v1/employee";

    public ResponseEntity<List<Employee>> getAllEmployeesWithErrorHandlingResponse() {
        try {
            ResponseEntity<List<Employee>> response = restTemplate.exchange(
                    EMPLOYEE_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Employee>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(response.getBody());
            } else {
                throw new RuntimeException("Failed to get employees: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching employees: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Employee> getEmployeeByIdWithErrorHandling(String id) {
        try {
            String url = String.format("%s/%s", EMPLOYEE_API_URL, id);
            ResponseEntity<Employee> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Employee>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response; // Return the entire ResponseEntity
            } else {
                throw new RuntimeException("Failed to get employee with ID " + id +
                        ". Status: " + response.getStatusCode() +
                        ", Response: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching employee with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public ResponseEntity<List<Employee>> searchEmployeesByNameLocallyResponse(String nameSearch) {
        try {
            ResponseEntity<List<Employee>> allEmployeesResponse = getAllEmployeesWithErrorHandlingResponse();

            if (allEmployeesResponse.getStatusCode().is2xxSuccessful() &&
                    allEmployeesResponse.getBody() != null) {

                // Filter the list
                List<Employee> filtered = allEmployeesResponse.getBody().stream()
                        .filter(employee -> employee.getEmployeeName() != null &&
                                employee.getEmployeeName().toLowerCase().contains(nameSearch.toLowerCase()))
                        .collect(Collectors.toList());

                return ResponseEntity.status(allEmployeesResponse.getStatusCode())
                        .headers(allEmployeesResponse.getHeaders())
                        .body(filtered);
            } else {
                throw new RuntimeException("Failed to retrieve employees for search. Status: " +
                        allEmployeesResponse.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while searching employees by name '" + nameSearch + "': " + e.getMessage(), e);
        }
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            // First get all employees
            ResponseEntity<List<Employee>> response = getAllEmployeesWithErrorHandlingResponse();

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Process and sort employees
                List<String> topEarners = response.getBody().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                        .limit(10)
                        .map(Employee::getEmployeeName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                return ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(topEarners);
            } else {
                throw new RuntimeException("Failed to get employees for top earners calculation: " +
                        response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating top earners: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            // First get all employees
            ResponseEntity<List<Employee>> response = getAllEmployeesWithErrorHandlingResponse();

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Calculate highest salary
                int maxSalary = response.getBody().stream()
                        .mapToInt(Employee::getEmployeeSalary)
                        .max()
                        .orElseThrow(() -> new RuntimeException("No employees found"));

                return ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(maxSalary);
            } else {
                throw new RuntimeException("Failed to get employees for salary calculation: " +
                        response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating highest salary: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<String> deleteEmployeeById(String id) {
        try {
            // 1. First get the employee to retrieve their name
            ResponseEntity<Employee> getResponse = restTemplate.exchange(
                    "http://localhost:8112/api/v1/employees/" + id,
                    HttpMethod.GET,
                    null,
                    Employee.class
            );

            if (!getResponse.getStatusCode().is2xxSuccessful() || getResponse.getBody() == null) {
                throw new RuntimeException("Employee with ID " + id + " not found");
            }

            String employeeName = getResponse.getBody().getEmployeeName();

            // 2. Delete the employee
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    "http://localhost:8112/api/v1/employees/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to delete employee with ID " + id);
            }

            // 3. Return the name of deleted employee
            return ResponseEntity.ok(employeeName);

        } catch (Exception e) {
            throw new RuntimeException("Error processing delete request for ID " + id + ": " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Employee> createEmployee(EmployeeRequest employeeInput) {
        try {
            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmployeeRequest> request = new HttpEntity<>(employeeInput, headers);

            // Make POST request
            ResponseEntity<Employee> response = restTemplate.exchange(
                    "http://localhost:8112/api/v1/employee",
                    HttpMethod.POST,
                    request,
                    Employee.class
            );

            // Verify successful creation
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response;
            } else {
                throw new RuntimeException("Failed to create employee. Status: " +
                        response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing create request: " +
                    e.getMessage(), e);
        }
    }
}