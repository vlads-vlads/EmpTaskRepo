package com.example.emp.business.repository;

import com.example.emp.business.repository.model.EmployeeDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeDAO, Long> {

    List<EmployeeDAO> findByDepartmentAndYearOfEmploymentAfter(String department, LocalDate yearOfEmployment);

    List<EmployeeDAO> findByDepartment(String department);

    List<EmployeeDAO> findByYearOfEmploymentAfter(LocalDate year);
}
