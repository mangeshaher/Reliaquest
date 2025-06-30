package com.reliaquest.api;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private final String API_URL = "http://localhost:8112/api/v1/employee";
    private Employee employee1;
    private Employee employee2;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        UUID employeeId1 = UUID.randomUUID();
        UUID employeeId2 = UUID.randomUUID();

        employee1 = new Employee();
        employee1.setId(employeeId1);
        employee1.setEmployeeName("John Doe");
        employee1.setEmployeeSalary(100000);
        employee1.setEmployeeAge(30);

        employee2 = new Employee();
        employee2.setId(employeeId2);
        employee2.setEmployeeName("Jane Smith");
        employee2.setEmployeeSalary(120000);
        employee2.setEmployeeAge(35);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setName("New Employee");
        employeeRequest.setSalary(80000);
        employeeRequest.setAge(25);
    }

    @Test
    void getAllEmployeesWithErrorHandlingResponse_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        ResponseEntity<List<Employee>> responseEntity = new ResponseEntity<>(employees, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<Employee>> response = employeeService.getAllEmployeesWithErrorHandlingResponse();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(restTemplate, times(1)).exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void getEmployeeByIdWithErrorHandling_Success() {
        String employeeId = "1";
        ResponseEntity<Employee> responseEntity = new ResponseEntity<>(employee1, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(API_URL + "/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<Employee> response = employeeService.getEmployeeByIdWithErrorHandling(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getEmployeeName());
    }

    @Test
    void searchEmployeesByNameLocallyResponse_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        ResponseEntity<List<Employee>> responseEntity = new ResponseEntity<>(employees, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<Employee>> response = employeeService.searchEmployeesByNameLocallyResponse("john");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getEmployeeName());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        ResponseEntity<List<Employee>> responseEntity = new ResponseEntity<>(employees, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<String>> response = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Jane Smith", response.getBody().get(0)); // Higher salary first
        assertEquals("John Doe", response.getBody().get(1));
    }

    @Test
    void getHighestSalaryOfEmployees_Success() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        ResponseEntity<List<Employee>> responseEntity = new ResponseEntity<>(employees, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<Integer> response = employeeService.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(120000, response.getBody());
    }

    @Test
    void deleteEmployeeById_Success() {
        String employeeId = "1";
        ResponseEntity<Employee> getResponse = new ResponseEntity<>(employee1, HttpStatus.OK);
        ResponseEntity<Void> deleteResponse = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:8112/api/v1/employees/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                eq(Employee.class)))
                .thenReturn(getResponse);

        when(restTemplate.exchange(
                eq("http://localhost:8112/api/v1/employees/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                eq(Void.class)))
                .thenReturn(deleteResponse);

        ResponseEntity<String> response = employeeService.deleteEmployeeById(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody());
    }

    @Test
    void createEmployee_Success() {
        ResponseEntity<Employee> responseEntity = new ResponseEntity<>(employee1, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Employee.class)))
                .thenReturn(responseEntity);

        ResponseEntity<Employee> response = employeeService.createEmployee(employeeRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getEmployeeName());
    }

    @Test
    void getAllEmployeesWithErrorHandlingResponse_Error() {
        when(restTemplate.exchange(
                eq(API_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("API Error"));

        assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployeesWithErrorHandlingResponse();
        });
    }

    @Test
    void getEmployeeByIdWithErrorHandling_NotFound() {
        String employeeId = "99";
        ResponseEntity<Employee> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq(API_URL + "/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployeeByIdWithErrorHandling(employeeId);
        });
    }
}