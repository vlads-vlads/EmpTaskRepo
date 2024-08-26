package com.example.emp.web.controller;

import com.example.emp.business.service.EmployeeService;
import com.example.emp.model.Employee;
import com.example.emp.swagger.HTMLResponseMessages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.emp.utils.ExportUtils.exportToCSV;
import static com.example.emp.utils.ExportUtils.exportToExcel;


@Log4j2
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @ApiOperation(value = "Get a list of employees",
            response = Employee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getEmployees(
            @ApiParam(value = "Department name to filter employees by") @RequestParam(required = false) String department,
            @ApiParam(value = "Year of hiring to filter employees by") @RequestParam(required = false) Integer year) {

        List<Employee> employeeList;

        if (department != null && year != null) {
            log.info("Retrieving a list of employees for department: {} and year: {}", department, year);
            employeeList = employeeService.getEmployees(department, year);
        } else {
            log.info("Retrieving a list of all employees");
            employeeList = employeeService.getAllEmployees();
        }

        log.debug("A list of employees is found. Size: {}", employeeList.size());
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "Saves the employee in the database",
            notes = "If provided employee is valid, saves it",
            response = Employee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee,
                                            BindingResult bindingResult) {
        log.info("Saving a new employee by passing: {}", employee);
        if(bindingResult.hasErrors()) {
            log.error("New employee is not saved: error {}", bindingResult);
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorMessages);
        }
        if(employee.getId() != null) {
            log.error("New employee is not saved: ID must not be included in the request");
            return ResponseEntity.badRequest().body("ID must not be included in the request for a new entity");
        }

        Employee savedEmployee = employeeService.addEmployee(employee);
        log.debug("New employee is saved: {}", employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes the employee by id",
            notes = "Deletes the employee if provided id exists",
            response = Employee.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITHOUT_DATA),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> deleteEmployee(@ApiParam(value = "The id of the intern hiring status", required = true)
                                                   @NonNull @PathVariable Long id) {
        log.info("Delete employee by ID, where ID is: {}", id);

        if(id <= 0) {
            log.warn("Received request with invalid ID: {}" , id);
            return ResponseEntity.badRequest().body("ID must be a positive number, provide ID is: " + id);
        }

        if (!employeeService.existsById(id)) {
            log.warn("Employee with ID {} not found.", id);
            return ResponseEntity.notFound().build();
        }

        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/export")
    public ResponseEntity<?> exportEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String format,
            HttpServletResponse response) {
        if (format == null || !List.of("csv", "xlsx").contains(format.toLowerCase())) {
            return ResponseEntity.badRequest().body("Invalid format. Please specify 'csv' or 'xlsx'.");
        }

        if (department == null || year == null) {
            return ResponseEntity.badRequest().body("Both 'department' and 'year' parameters are required.");
        }

        List<Employee> employees = employeeService.getEmployees(department, year);

        if (employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No employees found for the given criteria.");
        }

        if ("csv".equalsIgnoreCase(format)) {
            exportToCSV(employees, response);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            exportToExcel(employees, response);
        }

        return ResponseEntity.ok().build();
    }
}
