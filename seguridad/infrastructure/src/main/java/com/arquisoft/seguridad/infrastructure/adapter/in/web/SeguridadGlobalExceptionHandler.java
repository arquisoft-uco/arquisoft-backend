package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.domain.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.exception.InvalidCredentialsException;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import com.arquisoft.seguridad.domain.exception.KeycloakUnavailableException;
import com.arquisoft.shared.web.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador de excepciones de dominio del módulo de seguridad.
 * Las excepciones cross-cutting (Spring Security, validación, fallback)
 * son gestionadas por {@code GlobalAppExceptionHandler} en shared:web.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.arquisoft.seguridad")
public class SeguridadGlobalExceptionHandler {

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

    @ExceptionHandler(KeycloakUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleKeycloakUnavailable(
            KeycloakUnavailableException ex,
            HttpServletRequest request) {

        log.error("Keycloak unreachable: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponseDTO.builder()
                        .error("Service Unavailable")
                        .message("El servicio de autenticacion no esta disponible temporalmente. Intente mas tarde.")
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
