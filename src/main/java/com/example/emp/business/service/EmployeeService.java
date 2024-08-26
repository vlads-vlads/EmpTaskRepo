package com.example.emp.business.service;

import com.example.emp.model.Employee;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(Long id);

    List<Employee> getEmployees(String department, Integer year);

    List<Employee> getAllEmployees();

    Employee addEmployee(Employee employee);

    void deleteEmployee(Long id);

    boolean existsById(Long id);
}
