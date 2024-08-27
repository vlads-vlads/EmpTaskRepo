# Employee Management System

## Overview

This application provides CRUD operations for employee information and supports exporting data to CSV or Excel format.

## Endpoints

### Get Employees

- **GET** `/employees`
- **Query Parameters**: `department` (optional), `year` (optional)
- **Response**: List of employees.

- ![image](https://github.com/user-attachments/assets/308f4f2a-4968-408e-b11c-31af6d1bfd51)


### Create Employee

- **POST** `/employees`
- **Request Body**: `Employee` object (id, name, department, yearOfEmployment)
- **Response**: Created employee object.

### Delete Employee

- **DELETE** `/employees/{id}`
- **Path Parameter**: `id` of the employee to delete
- **Response**: Status 204 No Content if successful, 404 Not Found if employee does not exist.

### Export Employees

- **GET** `/employees/export`
- **Query Parameters**: `department` (optional), `year` (optional), `format` (`csv` or `xlsx`)
- **Response**: Downloadable file in specified format.

## Running the Application

1. Ensure Oracle database is running and configured as per `application.properties`.
2. Run the application using `mvn spring-boot:run` or your IDE.
3. Access endpoints via `http://localhost:8080`.

## Testing Endpoints

Use tools like Postman or curl to test the API endpoints. For example:

- **GET** `/employees?department=Digital&year=2023`
- **POST** `/employees` with a JSON body:
- {
  "name": "Saul",
  "department": "Finance",
  "yearOfEmployment": 2020-01-01
}
- **DELETE** `/employees/{id}`
- **GET** `/employees/export?format=csv`

## Exception Handling

- **404 Not Found**: When an employee record is not found.
