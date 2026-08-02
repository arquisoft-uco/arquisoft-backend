package com.arquisoft.seguridad.infrastructure.web;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.infrastructure.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SeguridadGlobalExceptionHandler {

    private final MessageCatalog catalog;

    public SeguridadGlobalExceptionHandler(MessageCatalog catalog) {
        this.catalog = catalog;
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> handleCredencialesInvalidas(
            CredencialesInvalidasException ex,
            HttpServletRequest request) {

        log.warn(catalog.obtener(SeguridadKeys.Login.LOG_CREDENCIALES_INVALIDAS_HANDLER),
                request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalog.obtener(SeguridadKeys.Login.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenInvalido(
            TokenInvalidoException ex,
            HttpServletRequest request) {

        log.warn(catalog.obtener(SeguridadKeys.Token.LOG_INVALIDO_HANDLER), request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalog.obtener(SeguridadKeys.Login.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAutenticacion(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn(catalog.obtener(SeguridadKeys.Login.LOG_EXCEPCION_AUTENTICACION), request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalog.obtener(SeguridadKeys.Login.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }
}
