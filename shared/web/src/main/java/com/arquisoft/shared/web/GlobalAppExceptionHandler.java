package com.arquisoft.shared.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones cross-cutting para toda la aplicación.
 * Cubre Spring Security, validación, parsing y fallback genérico.
 *
 * <p>Se registra con la menor precedencia ({@code LOWEST_PRECEDENCE}) para que
 * los handlers de cada contexto (anotados con {@code basePackages}) siempre
 * ganen si capturan la misma excepción.</p>
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAppExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        log.warn("Authorization denied (method-level): {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error("Forbidden")
                        .message("No tienes permisos para acceder a este recurso")
                        .status(HttpStatus.FORBIDDEN.value())
                        .path(request.getRequestURI())
                        .build());
    }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> ErrorResponseDTO.FieldErrorDTO.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .rejectedValue(fe.getRejectedValue())
                        .build())
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        log.warn("Constraint violation in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error("Parámetros inválidos")
                        .errorCode("PARAMETRO_INVALIDO")
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed request body in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error("Bad Request")
                        .message("Cuerpo de la petición mal formado")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAccess(
            ResourceAccessException ex,
            HttpServletRequest request) {

        log.error("External service connection error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponseDTO.builder()
                        .error("Service Unavailable")
                        .message("Error al conectar con servicio externo. Intente más tarde.")
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error in {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .error("Internal Server Error")
                        .message("Error interno del servidor")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
