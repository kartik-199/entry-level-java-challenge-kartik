package com.challenge.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImpl;
import com.challenge.api.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeImpl mockEmployee1;
    private EmployeeImpl mockEmployee2;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();

        mockEmployee1 = new EmployeeImpl();
        mockEmployee1.setUuid(testUuid);
        mockEmployee1.setFirstName("John");
        mockEmployee1.setLastName("Doe");
        mockEmployee1.setFullName("John Doe");
        mockEmployee1.setEmail("john.doe@example.com");
        mockEmployee1.setJobTitle("Software Engineer");
        mockEmployee1.setSalary(120000);

        mockEmployee2 = new EmployeeImpl();
        mockEmployee2.setUuid(UUID.randomUUID());
        mockEmployee2.setFirstName("Jane");
        mockEmployee2.setLastName("Smith");
        mockEmployee2.setFullName("Jane Smith");
        mockEmployee2.setEmail("jane.smith@example.com");
        mockEmployee2.setJobTitle("Product Manager");
        mockEmployee2.setSalary(130000);
    }

    @Test
    void testGetAllEmployees_Success() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(mockEmployee1, mockEmployee2));

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    void testGetAllEmployees_EmptyList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllEmployees_ServiceThrowsException() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/employee")).andExpect(status().isInternalServerError());
    }

    @Test
    void testGetEmployeeByUuid_Success() throws Exception {
        when(employeeService.getEmployee(testUuid)).thenReturn(Optional.of(mockEmployee1));

        mockMvc.perform(get("/api/v1/employee/" + testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.jobTitle").value("Software Engineer"))
                .andExpect(jsonPath("$.salary").value(120000));
    }

    @Test
    void testGetEmployeeByUuid_NotFound() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        when(employeeService.getEmployee(nonExistentUuid)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employee/" + nonExistentUuid)).andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeeByUuid_InvalidUuidFormat() throws Exception {
        mockMvc.perform(get("/api/v1/employee/invalid-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(mockEmployee1);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testCreateEmployee_WithAllFields() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(mockEmployee1);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"," + "\"email\":\"john.doe@example.com\","
                                + "\"jobTitle\":\"Software Engineer\","
                                + "\"salary\":120000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.jobTitle").value("Software Engineer"))
                .andExpect(jsonPath("$.salary").value(120000));
    }

    @Test
    void testCreateEmployee_MissingFirstName() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastName\":\"Doe\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_MissingLastName() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_EmptyFirstName() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_EmptyLastName() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_NullRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_InvalidJsonFormat() throws Exception {
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_WithNumericSalary() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(mockEmployee1);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"salary\":75000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salary").value(120000));
    }

    @Test
    void testCreateEmployee_OnlyRequiredFields() throws Exception {
        EmployeeImpl minimalEmployee = new EmployeeImpl();
        minimalEmployee.setFirstName("John");
        minimalEmployee.setLastName("Doe");
        minimalEmployee.setFullName("John Doe");

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(minimalEmployee);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
}
