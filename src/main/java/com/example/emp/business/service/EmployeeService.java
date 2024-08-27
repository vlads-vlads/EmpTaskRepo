package com.example.emp.business.service;

import com.example.emp.model.Employee;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(Long id);

    List<Employee> getEmployees(String department, Integer year);

    Employee addEmployee(Employee employee);

    void deleteEmployee(Long id);

    boolean existsById(Long id);

    void exportToCSV(List<Employee> employees, HttpServletResponse response);

    void exportToExcel(List<Employee> employees, HttpServletResponse response);
}
