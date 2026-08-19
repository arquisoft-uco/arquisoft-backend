package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.Mensajes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

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

        log.warn(Mensajes.obtener(IniciarSesionKey.LOG_UNAUTHORIZED), request.getRequestURI(), authException.getMessage());
        resolver.resolveException(request, response, null, authException);
    }
}
