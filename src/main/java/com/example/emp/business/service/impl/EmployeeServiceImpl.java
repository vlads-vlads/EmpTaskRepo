package com.example.emp.business.service.impl;

import com.example.emp.business.handlers.EmployeeNotFoundException;
import com.example.emp.business.mappers.EmployeeMapStructMapper;
import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeMapStructMapper employeeMapStructMapper;

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(employeeMapStructMapper::employeeDAOToEmployee)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee is not found or has left the organization."));

    }

    @Override
    public List<Employee> getEmployees(String department, Integer year) {
        List<EmployeeDAO> employeeDAOList = employeeRepository
                .findByDepartmentAndYearOfEmployment(department, year);
        log.info("Get filtered employee list by department and year of employment. Size is: {}", employeeDAOList.size());
        return employeeDAOList.stream()
                .map(employeeMapStructMapper::employeeDAOToEmployee).collect(Collectors.toList());
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<EmployeeDAO> employeeDAOList = employeeRepository.findAll();
        log.info("Get employee list. Size is: {}", employeeDAOList.size());
        return employeeDAOList.stream()
                .map(employeeMapStructMapper::employeeDAOToEmployee).collect(Collectors.toList());
    }

    @Override
    public Employee addEmployee(Employee employee) {
        EmployeeDAO savedEmployeeDAO = employeeRepository
                .save(employeeMapStructMapper.employeeToEmployeeDAO(employee));
        return employeeMapStructMapper.employeeDAOToEmployee(savedEmployeeDAO);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        log.info("Employee with id {} is deleted", id);
    }

    @Override
    public boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }
}
