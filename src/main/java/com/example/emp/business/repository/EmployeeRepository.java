package com.example.emp.business.repository;

import com.example.emp.business.repository.model.EmployeeDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeDAO, Long> {

    List<EmployeeDAO> findByDepartmentAndYearOfEmploymentAfter(String department, Integer yearOfEmployment);

    List<EmployeeDAO> findByDepartment(String department);

    List<EmployeeDAO> findByYearOfEmployment(Integer year);

    Optional<Object> findByName(String aliceSmith);
}
