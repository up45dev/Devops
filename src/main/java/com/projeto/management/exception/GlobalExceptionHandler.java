package com.projeto.management.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Entidade não encontrada: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Entidade Não Encontrada")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.error("Erro de negócio: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de Negócio")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Erro de validação: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de Validação")
            .message("Dados inválidos fornecidos")
            .path(request.getDescription(false).replace("uri=", ""))
            .validationErrors(validationErrors)
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erro interno do servidor", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Erro Interno do Servidor")
            .message("Ocorreu um erro inesperado")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Classe para resposta de erro padronizada
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;
        
        // Construtores
        public ErrorResponse() {}
        
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        // Getters e Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
        
        // Builder
        public static class ErrorResponseBuilder {
            private final ErrorResponse errorResponse = new ErrorResponse();
            
            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                errorResponse.setTimestamp(timestamp);
                return this;
            }
            
            public ErrorResponseBuilder status(int status) {
                errorResponse.setStatus(status);
                return this;
            }
            
            public ErrorResponseBuilder error(String error) {
                errorResponse.setError(error);
                return this;
            }
            
            public ErrorResponseBuilder message(String message) {
                errorResponse.setMessage(message);
                return this;
            }
            
            public ErrorResponseBuilder path(String path) {
                errorResponse.setPath(path);
                return this;
            }
            
            public ErrorResponseBuilder validationErrors(Map<String, String> validationErrors) {
                errorResponse.setValidationErrors(validationErrors);
                return this;
            }
            
            public ErrorResponse build() {
                return errorResponse;
            }
        }
    }
}
