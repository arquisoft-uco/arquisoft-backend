package com.arquisoft.seguridad.infrastructure.web;

import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.seguridad.application.auth.exception.AutenticacionException;
import com.arquisoft.seguridad.infrastructure.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SeguridadGlobalExceptionHandler {

    private final AppLogger logger;


    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> handleCredencialesInvalidas(
            CredencialesInvalidasException ex,
            HttpServletRequest request) {

        logger.warn(Mensajes.obtener(IniciarSesionKey.LOG_CREDENCIALES_INVALIDAS_HANDLER),
                request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenInvalido(
            TokenInvalidoException ex,
            HttpServletRequest request) {

        logger.warn(Mensajes.obtener(TokenKey.LOG_INVALIDO_HANDLER), request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(AutenticacionException.class)
    public ResponseEntity<ErrorResponseDTO> handleAutenticacion(
            AutenticacionException ex,
            HttpServletRequest request) {

        logger.warn(Mensajes.obtener(IniciarSesionKey.LOG_EXCEPCION_AUTENTICACION), request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }
}
