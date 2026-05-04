package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.shared.exceptions.DomainException;
import com.arquisoft.shared.web.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador de excepciones de dominio del módulo de fichas.
 * Las excepciones cross-cutting (validación, Spring Security, fallback)
 * son gestionadas por {@code GlobalAppExceptionHandler} en shared:web.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.arquisoft.fichas")
public class FichasGlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomainGeneric(
            DomainException ex,
            HttpServletRequest request) {

        log.warn("Error de dominio en {}: [{}] {}", request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponseDTO.builder()
                        .error("Error de dominio")
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
