package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.key.seguridad.LoginKey;
import com.arquisoft.shared.message.MessageCatalog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;
    private final MessageCatalog catalog;

    public SecurityAccessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            MessageCatalog catalog) {
        this.resolver = resolver;
        this.catalog = catalog;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        log.warn(catalog.obtener(LoginKey.LOG_ACCESS_DENIED), request.getRequestURI(), accessDeniedException.getMessage());
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
