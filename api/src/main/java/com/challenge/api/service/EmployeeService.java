package com.challenge.api.service;

import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImpl;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;

/* This is a mock service that is simulating simple CRUD for Employees.
We could connect this to a DB or some sort of provider in a real-life implementation. */

@Service
public class EmployeeService {
    private Map<UUID, Employee> employeeMap = new HashMap<>();

    public EmployeeService() {
        EmployeeImpl e1 = new EmployeeImpl();
        e1.setUuid(UUID.randomUUID());
        e1.setFirstName("Johnny");
        e1.setLastName("Appleseed");
        e1.setJobTitle("Principal Software Engineer");
        e1.setSalary(124000);
        e1.setAge(29);
        e1.setContractHireDate(Instant.now());

        EmployeeImpl e2 = new EmployeeImpl();
        e2.setUuid(UUID.randomUUID());
        e2.setFirstName("Rahul");
        e2.setLastName("Gupta");
        e2.setJobTitle("IT Director");
        e2.setSalary(167000);
        e2.setAge(39);
        e2.setContractHireDate(Instant.now());

        employeeMap.put(e1.getUuid(), e1);
        employeeMap.put(e2.getUuid(), e2);
    }

    // Service functions to interact with the data -> what the API endpoints will use
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public Optional<Employee> getEmployee(UUID uuid) {
        return Optional.ofNullable(employeeMap.get(uuid));
    }

    public Employee createEmployee(Employee employee) {
        employee.setUuid(UUID.randomUUID());
        employee.setContractHireDate(Instant.now());
        employeeMap.put(employee.getUuid(), employee);
        return employee;
    }
}
