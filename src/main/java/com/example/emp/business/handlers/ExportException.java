package com.example.emp.business.handlers;

public class ExportException extends RuntimeException{
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
