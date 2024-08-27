package com.example.emp.web.controller;

import com.example.emp.business.repository.EmployeeRepository;
import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    public void testGetEmployees() throws Exception {
        EmployeeDAO employeeDAO = new EmployeeDAO(null, "John Doe", "Engineering", LocalDate.of(2020, 1, 1));
        employeeRepository.save(employeeDAO);

        mockMvc.perform(get("/employees")
                        .param("department", "Engineering")
                        .param("year", "2020-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetEmployeesWithNoResults() throws Exception {
        mockMvc.perform(get("/employees")
                        .param("department", "Nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Employee employee = new Employee(null, "Jane Doe", "HR", LocalDate.of(2021, 1, 1));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    public void testCreateEmployeeWithValidationErrors() throws Exception {
        Employee employee = new Employee(null, "", "HR", LocalDate.of(2021, 1, 1)); // Invalid name

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateEmployeeWithId() throws Exception {
        Employee employee = new Employee(1L, "Jane Doe", "HR", LocalDate.of(2021, 1, 1));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        EmployeeDAO employeeDAO = employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));

        mockMvc.perform(delete("/employees/{id}", employeeDAO.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<EmployeeDAO> deletedEmployee = employeeRepository.findById(employeeDAO.getId());
        assertEquals(Optional.empty(), deletedEmployee);
    }

    @Test
    public void testDeleteNonexistentEmployee() throws Exception {
        mockMvc.perform(delete("/employees/{id}", 999L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testExportEmployees() throws Exception {
        employeeRepository.save(new EmployeeDAO(1L, "John Doe", "Engineering", LocalDate.of(2020, 1, 1)));

        mockMvc.perform(get("/employees/export")
                        .param("format", "csv"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testExportEmployeesWithInvalidFormat() throws Exception {
        mockMvc.perform(get("/employees/export")
                        .param("format", "invalid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid format. Please specify 'csv' or 'xlsx'."));
    }

    @Test
    public void testExportEmployeesNoResults() throws Exception {
        mockMvc.perform(get("/employees/export")
                        .param("format", "csv"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string("No employees found for the given criteria."));
    }
}