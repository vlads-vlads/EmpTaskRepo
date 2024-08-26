package com.example.emp.business.mappers;

import com.example.emp.business.repository.model.EmployeeDAO;
import com.example.emp.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapStructMapper {

    Employee employeeDAOToEmployee(EmployeeDAO employeeDAO);

    EmployeeDAO employeeToEmployeeDAO(Employee employee);
}
