package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.SeguridadMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Entry point de autenticación que delega al {@link HandlerExceptionResolver} de Spring MVC,
 * garantizando que los 401 generados a nivel de filtro (token ausente, expirado o inválido)
 * devuelvan un body {@code ErrorResponseDTO} consistente con el resto de la API,
 * en lugar del comportamiento por defecto que solo escribe el header {@code WWW-Authenticate}.
 */
@Slf4j
@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public SecurityAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        log.warn(SeguridadMessages.Login.LOG_UNAUTHORIZED, request.getRequestURI(), authException.getMessage());
        resolver.resolveException(request, response, null, authException);
    }
}
