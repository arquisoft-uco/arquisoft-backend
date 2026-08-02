package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.SeguridadKeys;
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
    private final MessageCatalog catalog;

    public SecurityAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            MessageCatalog catalog) {
        this.resolver = resolver;
        this.catalog = catalog;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        log.warn(catalog.obtener(SeguridadKeys.Login.LOG_UNAUTHORIZED), request.getRequestURI(), authException.getMessage());
        resolver.resolveException(request, response, null, authException);
    }
}
