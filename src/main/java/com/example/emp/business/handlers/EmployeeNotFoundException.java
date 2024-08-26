package com.example.emp.business.handlers;

public class EmployeeNotFoundException extends  RuntimeException{
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}

