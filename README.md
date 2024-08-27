# Employee Management System

## Overview

This application provides CRUD operations for employee information and supports exporting data to CSV or Excel format.

## Endpoints

### Get Employees

- **GET** `/employees`
- **Query Parameters**: `department` (optional), `year` (optional)
- **Response**: List of employees.
  
-returns an empty list of employees, if there aren't any
- ![image](https://github.com/user-attachments/assets/308f4f2a-4968-408e-b11c-31af6d1bfd51)

- returns a full list with all employees (no filters)
- ![image](https://github.com/user-attachments/assets/a58615d2-2dcf-4635-87a3-6d96881262f4)

- returns a filtered list by year (yearOfEmployment after provided)
- ![image](https://github.com/user-attachments/assets/fd3f522b-92d6-4c96-be07-7bd33bdccea9)

- returns a filtered list by department
- ![image](https://github.com/user-attachments/assets/efeea37d-a502-46f0-9477-60ded958190c)

- returns a filtered list by both parameters (department, yearOfEmployment)
- ![image](https://github.com/user-attachments/assets/025067f7-d32d-4280-9d19-1d3028d65656)

- returns a 400 status code, if parameter is invalid
- ![image](https://github.com/user-attachments/assets/3c0cbedf-fa43-4ab9-aed0-4752a3515a6c)



### Create Employee

- **POST** `/employees`
- **Request Body**: `Employee` object (id, name, department, yearOfEmployment)
- **Response**: Created employee object.

- creates a new empployee and returns 201 status code
- ![image](https://github.com/user-attachments/assets/9784c5b8-50ef-4985-9824-ce2e2f208d79)

- returns 400 status code, if body contains ID
- ![image](https://github.com/user-attachments/assets/8ff2029e-fa13-4424-9f53-76abd7c9fec6)

- returns 400 status code, in case if field validation failed
- ![image](https://github.com/user-attachments/assets/13e96459-fd82-4272-8e40-ba4215fca672)



### Delete Employee

- **DELETE** `/employees/{id}`
- **Path Parameter**: `id` of the employee to delete
- **Response**: Status 204 No Content if successful, 404 Not Found if employee does not exist.

- deletes an employee, if provided ID exists and returns 204 status code
- ![image](https://github.com/user-attachments/assets/28a245f9-d772-42e4-9d61-4fdc72b058f2)

- returns 400 status code, if provided ID fails validation
- ![image](https://github.com/user-attachments/assets/ed632e69-1c70-49be-b168-844c3474c4b3)

- returns 404 status code, if employee with provided ID is not found
- ![image](https://github.com/user-attachments/assets/c1379f82-9f75-4740-af9f-f0f347179758)



### Export Employees

- **GET** `/employees/export`
- **Query Parameters**: `department` (optional), `year` (optional), `format` (`csv` or `xlsx`)
- **Response**: Downloadable file in specified format.

- returns content and download a csv file (with provided department, yearOfEmployment and format)
- ![image](https://github.com/user-attachments/assets/f5233487-a9b1-44c7-91db-a34542d68eb8)
- ![image](https://github.com/user-attachments/assets/cfcc16c9-b4cd-4949-b600-704511237b93)
- ![image](https://github.com/user-attachments/assets/17a9d714-c365-4fbc-a5ce-9896705791e7)

- returns 400 status code, if provided format isn't supported (pdf)
- ![image](https://github.com/user-attachments/assets/fbb8e118-20b6-4bf6-a26a-b11a9096526f)

- returns content and download a csv file without filters (csv by default)
- ![image](https://github.com/user-attachments/assets/4dd42bfe-4796-4337-9597-d1d4e98c0afe)
- ![image](https://github.com/user-attachments/assets/aecd299d-94b0-4d06-a5fd-ed673851d638)

- return content (not readable in Postman) and download a xlsx file (with provided department)
- ![image](https://github.com/user-attachments/assets/4659b7c8-9da2-4bb2-986a-412b7701c701)
- ![image](https://github.com/user-attachments/assets/5783361b-d586-419b-91f3-5219a560a0a4)



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
