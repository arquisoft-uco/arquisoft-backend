package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.CatalogoMensajes;
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
    private final CatalogoMensajes catalogo;

    public SecurityAccessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            CatalogoMensajes catalogo) {
        this.resolver = resolver;
        this.catalogo = catalogo;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        log.warn(catalogo.obtener(IniciarSesionKey.LOG_ACCESS_DENIED), request.getRequestURI(), accessDeniedException.getMessage());
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
