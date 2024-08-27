package com.example.emp.business.service.impl;

import com.example.emp.business.handlers.EmployeeNotFoundException;
import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class EmployeeServiceImplIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    public void testAddEmployee() {
        Employee employee = new Employee(null, "Jane Doe", "HR", 2021);

        Employee savedEmployee = employeeService.addEmployee(employee);

        assertEquals("Jane Doe", savedEmployee.getName());
        assertEquals("HR", savedEmployee.getDepartment());
        assertEquals(2021, savedEmployee.getYearOfEmployment());
    }

    @Test
    public void testGetEmployeeById() {

        EmployeeDAO savedEmployeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", 2020));

        Employee employee = employeeService.getEmployeeById(savedEmployeeDAO.getId());

        assertNotNull(employee);
        assertEquals("John Doe", employee.getName());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    public void testGetEmployeeByIdNotFound() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
    }

    @Test
    public void testGetEmployees() {
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", 2020));
        employeeRepository.save(new EmployeeDAO(2L, "Jane Doe", "HR", 2021));

        List<Employee> employees = employeeService.getEmployees("Engineering", null);

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }

    @Test
    public void testGetEmployeesWithNoResults() {
        List<Employee> employees = employeeService.getEmployees("Nonexistent", null);

        assertEquals(0, employees.size());
    }

    @Test
    public void testDeleteEmployee() {
        EmployeeDAO employeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", 2020));

        employeeService.deleteEmployee(1L);

        assertEquals(false, employeeRepository.existsById(1L));
    }

    @Test
    public void testDeleteNonexistentEmployee() {

        employeeService.deleteEmployee(999L);
    }

}
