package com.challenge.api.controller;

import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImpl;
import com.challenge.api.service.EmployeeService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Fill in the missing aspects of this Spring Web REST Controller. Don't forget to add a Service layer.
 */
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    // Create instance of EmployeeService
    private final EmployeeService employeeService;

    /**
     * @implNote Need not be concerned with an actual persistence layer. Generate mock Employee models as necessary.
     * @return One or more Employees.
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET request for all employee data
    @GetMapping
    public List<Employee> getAllEmployees() {
        try {
            return employeeService.getAllEmployees();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @implNote Need not be concerned with an actual persistence layer. Generate mock Employee model as necessary.
     * @param uuid Employee UUID
     * @return Requested Employee if exists
     */
    // GET request for employee with specific uuid
    @GetMapping("/{uuid}")
    public Employee getEmployeeByUuid(@PathVariable UUID uuid) {
        return employeeService
                .getEmployee(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    /**
     * @implNote Need not be concerned with an actual persistence layer.
     * @param requestBody hint!
     * @return Newly created Employee
     */
    // POST request for a new employee
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@RequestBody Object requestBody) {
        if (requestBody == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee request body is null");
        }
        Employee employeeData;
        // Try casting as Employee or mapping of attributes, and then add Employee
        try {
            if (requestBody instanceof Employee) {
                employeeData = (Employee) requestBody;
            } else if (requestBody instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) requestBody;
                employeeData = mapToEmployee(map);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Employee request body is not of type Map or Employee");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (employeeData.getFirstName() == null
                || employeeData.getLastName() == null
                || employeeData.getFirstName().trim().isEmpty()
                || employeeData.getLastName().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee request name is null");
        return employeeService.createEmployee(employeeData);
    }

    // Helper function for mapping attributes
    private Employee mapToEmployee(Map<String, Object> map) {
        Employee employee = new EmployeeImpl();
        if (map.containsKey("firstName")) {
            employee.setFirstName((String) map.get("firstName"));
        }
        if (map.containsKey("lastName")) {
            employee.setLastName((String) map.get("lastName"));
        }
        if (map.containsKey("email")) {
            employee.setEmail((String) map.get("email"));
        }
        if (map.containsKey("jobTitle")) {
            employee.setJobTitle((String) map.get("jobTitle"));
        }
        if (map.containsKey("salary")) {
            Object salaryobj = map.get("salary");
            if (salaryobj instanceof Number) {
                employee.setSalary(((Number) salaryobj).intValue());
            }
        }
        if (employee.getFirstName() != null && employee.getLastName() != null) {
            employee.setFullName(employee.getFirstName() + " " + employee.getLastName());
        }
        return employee;
    }
}
