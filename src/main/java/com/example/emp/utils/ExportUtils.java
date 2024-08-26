package com.example.emp.utils;

import com.example.emp.business.handlers.ExportException;
import com.example.emp.model.Employee;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class ExportUtils {

    public static void exportToCSV(List<Employee> employees, HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=employees.csv");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Name", "Department"});
            for (Employee emp : employees) {
                writer.writeNext(new String[]{
                        String.valueOf(emp.getId()),
                        emp.getName(),
                        emp.getDepartment()
                });
            }
        } catch (IOException e) {
            throw new ExportException("Failed to export to CSV", e);
        }
    }

    public static void exportToExcel(List<Employee> employees, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Department");

            int rowNum = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getDepartment());
            }

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new ExportException("Failed to export to Excel", e);
        }
    }
}

