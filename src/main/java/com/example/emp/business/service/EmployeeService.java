package com.example.emp.business.service;

import com.example.emp.model.Employee;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(Long id);

    List<Employee> getEmployees(String department, LocalDate year);

    Employee addEmployee(Employee employee);

    void deleteEmployee(Long id);

    boolean existsById(Long id);

    void exportToCSV(List<Employee> employees, HttpServletResponse response);

    void exportToExcel(List<Employee> employees, HttpServletResponse response);
}
