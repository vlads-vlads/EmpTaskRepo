package com.example.emp.business.repository;

import com.example.emp.business.repository.model.EmployeeDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeDAO, Long> {

    List<EmployeeDAO> findByDepartmentAndYearOfEmployment(String department, Integer yearOfEmployment);
}
