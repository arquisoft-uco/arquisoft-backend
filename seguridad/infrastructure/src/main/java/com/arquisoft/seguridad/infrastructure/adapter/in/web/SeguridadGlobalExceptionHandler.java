package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.domain.exception.AuthenticationException;
import com.arquisoft.shared.web.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Manejador de excepciones del módulo de seguridad.
 *
 * <p>Cubre la jerarquía de {@link AuthenticationException} ({@code InvalidCredentialsException},
 * {@code InvalidTokenException}) usando polimorfismo — un único handler captura todos los
 * subtipos y los mapea a {@code 401 Unauthorized}.</p>
 *
 * <p>Las excepciones de infraestructura ({@code KeycloakUnavailableException}) y las
 * cross-cutting (Spring Security, validación, fallback) son gestionadas por
 * {@code GlobalAppExceptionHandler} en {@code shared:web}.</p>
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.arquisoft.seguridad")
public class SeguridadGlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication exception in {}: [{}] {}", request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        List<String> trace = ex.getError().getTrace().isEmpty() ? null : ex.getError().getTrace();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error("Unauthorized")
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .trace(trace)
                        .build());
    }
}
