package com.arquisoft.shared.security.infrastructure.rest;

import com.arquisoft.shared.security.domain.dto.ErrorResponseDTO;
import com.arquisoft.shared.security.domain.exceptions.AuthenticationException;
import com.arquisoft.shared.security.domain.exceptions.InvalidCredentialsException;
import com.arquisoft.shared.security.domain.exceptions.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para el módulo de seguridad.
 * Proporciona respuestas de error estandarizadas para todas las excepciones.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de credenciales inválidas.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException ex, 
            HttpServletRequest request) {
        
        log.warn("Invalid credentials: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error("Unauthorized")
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja excepciones de token inválido.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidToken(
            InvalidTokenException ex, 
            HttpServletRequest request) {
        
        log.warn("Invalid token: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error("Unauthorized")
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja excepciones de autenticación genéricas.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(
            AuthenticationException ex, 
            HttpServletRequest request) {
        
        log.error("Authentication error: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error("Unauthorized")
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja excepciones de acceso denegado (roles insuficientes).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex, 
            HttpServletRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error("Forbidden")
                        .message("No tienes permisos para acceder a este recurso")
                        .status(HttpStatus.FORBIDDEN.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja excepciones de validación de Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        log.warn("Validation error: {} field(s) with errors", fieldErrors.size());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error("Bad Request")
                        .message("Error de validación en los datos enviados")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .fieldErrors(fieldErrors)
                        .build());
    }

    /**
     * Maneja excepciones de parámetros faltantes.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(
            MissingServletRequestParameterException ex, 
            HttpServletRequest request) {
        
        log.warn("Missing parameter: {}", ex.getParameterName());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error("Bad Request")
                        .message("Parámetro requerido faltante: " + ex.getParameterName())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja excepciones de conexión con servicios externos (Keycloak).
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAccess(
            ResourceAccessException ex, 
            HttpServletRequest request) {
        
        log.error("External service connection error: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponseDTO.builder()
                        .error("Service Unavailable")
                        .message("Error al conectar con el servidor de autenticación. Intente más tarde.")
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Maneja cualquier otra excepción no controlada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex, 
            HttpServletRequest request) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .error("Internal Server Error")
                        .message("Error interno del servidor")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .build());
    }

    /**
     * Mapea un FieldError de Spring a nuestro DTO.
     */
    private ErrorResponseDTO.FieldErrorDTO mapFieldError(FieldError fieldError) {
        return ErrorResponseDTO.FieldErrorDTO.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
}
