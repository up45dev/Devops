package com.taskmanagement.projeto.dto;

/**
 * DTO para resposta de API genérica
 */
public class ApiResponse {
    
    private boolean success;
    private String message;
    
    // Constructors
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}