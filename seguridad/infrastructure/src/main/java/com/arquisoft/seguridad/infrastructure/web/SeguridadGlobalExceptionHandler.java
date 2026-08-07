package com.arquisoft.seguridad.infrastructure.web;

import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.CatalogoMensajes;
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

    private final CatalogoMensajes catalogo;

    public SeguridadGlobalExceptionHandler(CatalogoMensajes catalogo) {
        this.catalogo = catalogo;
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> handleCredencialesInvalidas(
            CredencialesInvalidasException ex,
            HttpServletRequest request) {

        log.warn(catalogo.obtener(IniciarSesionKey.LOG_CREDENCIALES_INVALIDAS_HANDLER),
                request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalogo.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenInvalido(
            TokenInvalidoException ex,
            HttpServletRequest request) {

        log.warn(catalogo.obtener(TokenKey.LOG_INVALIDO_HANDLER), request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalogo.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAutenticacion(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn(catalogo.obtener(IniciarSesionKey.LOG_EXCEPCION_AUTENTICACION), request.getRequestURI(), ex.getCodigoError(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.fromBaseException(
                        ex, catalogo.obtener(IniciarSesionKey.ERROR_HTTP_401), HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }
}
