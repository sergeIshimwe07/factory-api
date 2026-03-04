package com.factory.factory_erp.repository;

import com.factory.factory_erp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByNames(String names);
    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}
