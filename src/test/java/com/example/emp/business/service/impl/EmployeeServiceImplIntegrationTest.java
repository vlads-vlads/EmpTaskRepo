package com.example.emp.business.service.impl;

import com.example.emp.business.handlers.EmployeeNotFoundException;
import com.example.emp.business.handlers.ExportException;
import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;


import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
        Employee employee = new Employee(null, "Jane Doe", "HR", LocalDate.of(2021, 1, 1));

        Employee savedEmployee = employeeService.addEmployee(employee);

        assertEquals("Jane Doe", savedEmployee.getName());
        assertEquals("HR", savedEmployee.getDepartment());
        assertEquals(LocalDate.of(2021, 1, 1), savedEmployee.getYearOfEmployment());
    }

    @Test
    public void testAddEmployeeWithNullValues() {
        Employee employee = new Employee(null, null, null, null);

        Employee savedEmployee = employeeService.addEmployee(employee);

        assertNotNull(savedEmployee);
        assertNull(savedEmployee.getName());
        assertNull(savedEmployee.getDepartment());
        assertNull(savedEmployee.getYearOfEmployment());
    }

    @Test
    public void testGetEmployeeById() {

        EmployeeDAO savedEmployeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2021, 1, 1)));

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
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));
        employeeRepository.save(new EmployeeDAO(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1)));

        List<Employee> employees = employeeService.getEmployees("Engineering", null);

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }

    @Test
    public void testGetEmployeesWithMultipleFilters() {
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));
        employeeRepository.save(new EmployeeDAO(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1)));
        employeeRepository.save(new EmployeeDAO(3L, "Jack Doe", "Engineering", LocalDate.of(2021, 1, 1)));

        List<Employee> employees = employeeService.getEmployees("Engineering", LocalDate.of(2020, 1, 1));

        assertEquals(1, employees.size());
        assertEquals("Jack Doe", employees.get(0).getName());
    }

    @Test
    public void testGetEmployeesWithNoFilters() {
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));
        employeeRepository.save(new EmployeeDAO(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1)));
        employeeRepository.save(new EmployeeDAO(3L, "Jack Doe", "Engineering", LocalDate.of(2021, 1, 1)));

        List<Employee> employees = employeeService.getEmployees(null, null);

        assertEquals(3, employees.size(), "There should be 3 employees");
    }

    @Test
    public void testGetEmployeesByYear() {
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));
        employeeRepository.save(new EmployeeDAO(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1)));

        List<Employee> employees = employeeService.getEmployees(null, LocalDate.of(2021, 1, 1));

        assertEquals(1, employees.size());
        assertEquals("Jane Doe", employees.get(0).getName());
    }



    @Test
    public void testGetEmployeesWithNoResults() {
        List<Employee> employees = employeeService.getEmployees("Nonexistent", null);

        assertEquals(0, employees.size());
    }

    @Test
    public void testDeleteEmployee() {
        EmployeeDAO employeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));

        employeeService.deleteEmployee(1L);

        assertEquals(false, employeeRepository.existsById(1L));
    }

    @Test
    public void testDeleteNonexistentEmployee() {

        employeeService.deleteEmployee(999L);
    }

    @Test
    public void testExistsById() {
        EmployeeDAO savedEmployeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));

        assertTrue(employeeService.existsById(savedEmployeeDAO.getId()), "Employee should exist");
        assertFalse(employeeService.existsById(999L), "Employee with ID 999 should not exist");
    }

    @Test
    public void testExportToCSV() throws IOException {
        List<Employee> employees = Arrays.asList(
                new Employee(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)),
                new Employee(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1))
        );

        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        employeeService.exportToCSV(employees, response);

        writer.flush();
        String output = stringWriter.toString();

        assertTrue(output.contains("\"ID\",\"Name\",\"Department\",\"YearOfEmployment\""), "CSV header is missing or incorrect.");
        assertTrue(output.contains("\"1\",\"John Doe\",\"Engineering\",\"2020-01-01\""), "CSV data for John Doe is missing or incorrect.");
        assertTrue(output.contains("\"2\",\"Jane Doe\",\"HR\",\"2021-01-01\""), "CSV data for Jane Doe is missing or incorrect.");
    }

    @Test
    public void testExportToCSVFailure() throws IOException {
        List<Employee> employees = Arrays.asList(
                new Employee(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1))
        );

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenThrow(new IOException("Mocked IOException"));

        assertThrows(ExportException.class, () -> employeeService.exportToCSV(employees, response));
    }

    @Test
    public void testExportToExcelFailure() throws IOException {
        List<Employee> employees = Arrays.asList(
                new Employee(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1))
        );

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenThrow(new IOException("Mocked IOException"));

        assertThrows(ExportException.class, () -> employeeService.exportToExcel(employees, response));
    }

    @Test
    public void testExportToExcel() throws IOException {
        List<Employee> employees = Arrays.asList(
                new Employee(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)),
                new Employee(2L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1))
        );

        MockHttpServletResponse response = new MockHttpServletResponse();

        employeeService.exportToExcel(employees, response);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should not be null");
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Department", headerRow.getCell(2).getStringCellValue());
            assertEquals("YearOfEmployment", headerRow.getCell(3).getStringCellValue());

            Row firstRow = sheet.getRow(1);
            assertNotNull(firstRow, "First data row should not be null");
            assertEquals(1, (int) firstRow.getCell(0).getNumericCellValue());
            assertEquals("John Doe", firstRow.getCell(1).getStringCellValue());
            assertEquals("Engineering", firstRow.getCell(2).getStringCellValue());
            assertEquals("2020-01-01", firstRow.getCell(3).getStringCellValue());

            Row secondRow = sheet.getRow(2);
            assertNotNull(secondRow, "Second data row should not be null");
            assertEquals(2, (int) secondRow.getCell(0).getNumericCellValue());
            assertEquals("Jane Doe", secondRow.getCell(1).getStringCellValue());
            assertEquals("HR", secondRow.getCell(2).getStringCellValue());
            assertEquals("2021-01-01", secondRow.getCell(3).getStringCellValue());
        }
    }

}
