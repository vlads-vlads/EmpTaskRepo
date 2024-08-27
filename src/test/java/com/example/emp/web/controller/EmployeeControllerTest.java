package com.example.emp.web.controller;


import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setDepartment("IT");
        employee.setYearOfEmployment(2020);
    }

    @Test
    void getEmployees_ReturnsOk() throws Exception {
        given(employeeService.getEmployees(null, null)).willReturn(Arrays.asList(employee));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(employee.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(employee.getName())))
                .andExpect(jsonPath("$[0].department", is(employee.getDepartment())))
                .andExpect(jsonPath("$[0].yearOfEmployment", is(employee.getYearOfEmployment())));

        verify(employeeService).getEmployees(null, null);
    }

    @Test
    void getEmployees_ReturnsEmptyList() throws Exception {
        given(employeeService.getEmployees(null, null)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(employeeService).getEmployees(null, null);
    }

    @Test
    void createEmployee_ReturnsCreated() throws Exception {
        Employee employeeWithoutId = new Employee();
        employeeWithoutId.setName("John Doe");
        employeeWithoutId.setDepartment("IT");
        employeeWithoutId.setYearOfEmployment(2020);

        Employee createdEmployee = new Employee();
        createdEmployee.setId(1L);
        createdEmployee.setName("John Doe");
        createdEmployee.setDepartment("IT");
        createdEmployee.setYearOfEmployment(2020);

        given(employeeService.addEmployee(any(Employee.class))).willReturn(createdEmployee);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeWithoutId)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(createdEmployee.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdEmployee.getName())))
                .andExpect(jsonPath("$.department", is(createdEmployee.getDepartment())))
                .andExpect(jsonPath("$.yearOfEmployment", is(createdEmployee.getYearOfEmployment())));

        verify(employeeService).addEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_ReturnsBadRequest_WhenValidationFails() throws Exception {
        Employee invalidEmployee = new Employee();
        invalidEmployee.setName("");  // invalid name

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());

        verify(employeeService, Mockito.never()).addEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_ReturnsBadRequest_WhenIdIsPresent() throws Exception {
        employee.setId(1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must not be included in the request for a new entity"));

        verify(employeeService, Mockito.never()).addEmployee(any(Employee.class));
    }

    @Test
    void deleteEmployee_ReturnsNoContent() throws Exception {
        given(employeeService.existsById(anyLong())).willReturn(true);
        doNothing().when(employeeService).deleteEmployee(anyLong());

        mockMvc.perform(delete("/employees/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_ReturnsBadRequest_ForInvalidId() throws Exception {
        mockMvc.perform(delete("/employees/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID must be a positive number, provide ID is: -1"));

        verify(employeeService, Mockito.never()).deleteEmployee(-1L);
    }

    @Test
    void deleteEmployee_ReturnsNotFound_WhenEmployeeDoesNotExist() throws Exception {
        given(employeeService.existsById(anyLong())).willReturn(false);

        mockMvc.perform(delete("/employees/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(employeeService).existsById(1L);
        verify(employeeService, Mockito.never()).deleteEmployee(1L);
    }

    @Test
    void exportEmployees_ReturnsOk_ForCSVFormat() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        given(employeeService.getEmployees(anyString(), anyInt())).willReturn(employees);
        doNothing().when(employeeService).exportToCSV(anyList(), any(HttpServletResponse.class));

        mockMvc.perform(get("/employees/export")
                        .param("department", "Digital")
                        .param("yearAfter", "2023")
                        .param("format", "csv"))
                .andExpect(status().isOk());

        verify(employeeService).exportToCSV(anyList(), any(HttpServletResponse.class));
    }

    @Test
    void exportEmployees_ReturnsOk_ForXLSXFormat() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        given(employeeService.getEmployees(anyString(), anyInt())).willReturn(employees);
        doNothing().when(employeeService).exportToExcel(any(), any(HttpServletResponse.class));

        mockMvc.perform(get("/employees/export")
                        .param("department", "Digital")
                        .param("yearAfter", "2023")
                        .param("format", "xlsx"))
                .andExpect(status().isOk());

        verify(employeeService).exportToExcel(any(), any(HttpServletResponse.class));
    }

    @Test
    void exportEmployees_ReturnsNoContent_WhenNoEmployeesFound() throws Exception {
        given(employeeService.getEmployees(anyString(), anyInt())).willReturn(Collections.emptyList());

        mockMvc.perform(get("/employees/export")
                        .param("department", "Digital")
                        .param("yearAfter", "2023")
                        .param("format", "csv"))
                .andExpect(status().isNoContent())
                .andExpect(content().string("No employees found for the given criteria."));

        verify(employeeService).getEmployees("Digital", 2023);
        verify(employeeService, Mockito.never()).exportToCSV(any(), any(HttpServletResponse.class));
    }

    @Test
    void exportEmployees_ReturnsBadRequest_ForInvalidFormat() throws Exception {
        mockMvc.perform(get("/employees/export")
                        .param("department", "Digital")
                        .param("yearAfter", "2023")
                        .param("format", "pdf"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid format. Please specify 'csv' or 'xlsx'."));

        verify(employeeService, Mockito.never()).getEmployees(anyString(), anyInt());
    }

}

