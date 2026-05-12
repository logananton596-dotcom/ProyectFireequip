package com.fireequipmanager.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Indica que esta clase manejará excepciones de todos los controladores
public class GlobalExceptionHandler {

    // 1. Capturar nuestras excepciones de negocio (Error 400)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Capturar errores de validación o argumentos ilegales (Error 400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Dato inválido: " + ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 3. Capturar cualquier otro error inesperado (Error 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Ocurrió un error interno en el servidor");
        response.put("error", ex.getMessage()); // Opcional: quitar en producción por seguridad
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}