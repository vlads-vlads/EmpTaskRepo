package com.example.emp.business.service.impl;

import com.example.emp.business.handlers.EmployeeNotFoundException;
import com.example.emp.business.handlers.ExportException;
import com.example.emp.business.mappers.EmployeeMapStructMapper;
import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        List<EmployeeDAO> employeeDAOList;

        if (department != null && year != null) {
            employeeDAOList = employeeRepository.findByDepartmentAndYearOfEmploymentAfter(department, year);
            log.info("Filtered by department and year. Size: {}", employeeDAOList.size());
        } else if (department != null) {
            employeeDAOList = employeeRepository.findByDepartment(department);
            log.info("Filtered by department only. Size: {}", employeeDAOList.size());
        } else if (year != null) {
            employeeDAOList = employeeRepository.findByYearOfEmployment(year);
            log.info("Filtered by year only. Size: {}", employeeDAOList.size());
        } else {
            employeeDAOList = employeeRepository.findAll();
            log.info("No filters applied. Returning all employees. Size: {}", employeeDAOList.size());
        }

        return employeeDAOList.stream()
                .map(employeeMapStructMapper::employeeDAOToEmployee)
                .collect(Collectors.toList());
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

    @Override
    public void exportToCSV(List<Employee> employees, HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=employees.csv");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Name", "Department", "YearOfEmployment"});
            for (Employee emp : employees) {
                writer.writeNext(new String[]{
                        String.valueOf(emp.getId()),
                        emp.getName(),
                        emp.getDepartment(),
                        String.valueOf(emp.getYearOfEmployment()),
                });
            }
        } catch (IOException e) {
            throw new ExportException("Failed to export to CSV", e);
        }
    }

    @Override
    public void exportToExcel(List<Employee> employees, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Department");
            headerRow.createCell(3).setCellValue("YearOfEmployment");

            int rowNum = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getDepartment());
                row.createCell(3).setCellValue(emp.getYearOfEmployment());
            }

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new ExportException("Failed to export to Excel", e);
        }
    }
}
