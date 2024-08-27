package com.example.emp.business.service.impl;

import com.example.emp.business.handlers.EmployeeNotFoundException;
import com.example.emp.business.mappers.EmployeeMapStructMapper;
import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.model.Employee;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapStructMapper employeeMapStructMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDAO employeeDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeDAO = new EmployeeDAO();
        employeeDAO.setId(1L);
        employeeDAO.setName("John Doe");
        employeeDAO.setDepartment("IT");
        employeeDAO.setYearOfEmployment(2020);

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setDepartment("IT");
        employee.setYearOfEmployment(2020);
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employeeDAO));
        when(employeeMapStructMapper.employeeDAOToEmployee(any(EmployeeDAO.class))).thenReturn(employee);

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getName(), result.getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });

        assertEquals("Employee is not found or has left the organization.", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEmployees_WithFilters() {
        List<EmployeeDAO> employeeDAOs = Arrays.asList(employeeDAO);
        when(employeeRepository.findByDepartmentAndYearOfEmploymentAfter(anyString(), anyInt())).thenReturn(employeeDAOs);
        when(employeeMapStructMapper.employeeDAOToEmployee(any(EmployeeDAO.class))).thenReturn(employee);

        List<Employee> result = employeeService.getEmployees("IT", 2019);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employee.getId(), result.get(0).getId());
        verify(employeeRepository, times(1)).findByDepartmentAndYearOfEmploymentAfter("IT", 2019);
    }

    @Test
    void testGetEmployees_EmptyRepository() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> result = employeeService.getEmployees(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testAddEmployee() {
        when(employeeMapStructMapper.employeeToEmployeeDAO(any(Employee.class))).thenReturn(employeeDAO);
        when(employeeRepository.save(any(EmployeeDAO.class))).thenReturn(employeeDAO);
        when(employeeMapStructMapper.employeeDAOToEmployee(any(EmployeeDAO.class))).thenReturn(employee);

        Employee result = employeeService.addEmployee(employee);

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        verify(employeeRepository, times(1)).save(employeeDAO);
    }

    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(anyLong());

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(employeeRepository.existsById(anyLong())).thenReturn(true);

        boolean result = employeeService.existsById(1L);

        assertTrue(result);
        verify(employeeRepository, times(1)).existsById(1L);
    }

    @Test
    void testExportToCSV() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        employeeService.exportToCSV(List.of(employee), response);

        String content = response.getContentAsString();

        assertNotNull(content);
        assertTrue(content.contains("\"ID\",\"Name\",\"Department\",\"YearOfEmployment\""));
        assertTrue(content.contains("\"1\",\"John Doe\",\"IT\",\"2020\""));

        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=employees.csv", response.getHeader("Content-Disposition"));
    }

    @Test
    void testExportToCSV_EmptyList() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        employeeService.exportToCSV(Collections.emptyList(), response);

        String content = response.getContentAsString();

        assertNotNull(content);
        assertTrue(content.contains("\"ID\",\"Name\",\"Department\",\"YearOfEmployment\""));
        assertFalse(content.contains("\"1\",\"John Doe\",\"IT\",\"2020\""));

        assertEquals("text/csv", response.getContentType());
        assertEquals("attachment; filename=employees.csv", response.getHeader("Content-Disposition"));
    }

    @Test
    void testExportToExcel() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        employeeService.exportToExcel(List.of(employee), response);

        byte[] content = response.getContentAsByteArray();
        assertNotNull(content, "The Excel content should not be null.");

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            Sheet sheet = workbook.getSheet("Employees");
            assertNotNull(sheet, "The 'Employees' sheet should not be null.");

            Row headerRow = sheet.getRow(0);
            assertEquals("ID", headerRow.getCell(0).getStringCellValue(), "The first header cell should be 'ID'.");
            assertEquals("Name", headerRow.getCell(1).getStringCellValue(), "The second header cell should be 'Name'.");
            assertEquals("Department", headerRow.getCell(2).getStringCellValue(), "The third header cell should be 'Department'.");
            assertEquals("YearOfEmployment", headerRow.getCell(3).getStringCellValue(), "The fourth header cell should be 'YearOfEmployment'.");

            Row dataRow = sheet.getRow(1);
            assertEquals(1L, (long) dataRow.getCell(0).getNumericCellValue(), "The first data cell should contain the ID '1'.");
            assertEquals("John Doe", dataRow.getCell(1).getStringCellValue(), "The second data cell should contain the name 'John Doe'.");
            assertEquals("IT", dataRow.getCell(2).getStringCellValue(), "The third data cell should contain the department 'IT'.");
            assertEquals(2020, (int) dataRow.getCell(3).getNumericCellValue(), "The fourth data cell should contain the year of employment '2020'.");
        }

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response.getContentType(), "The response content type should be 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'.");
        assertEquals("attachment; filename=employees.xlsx", response.getHeader("Content-Disposition"), "The content-disposition header should be 'attachment; filename=employees.xlsx'.");
    }


}
