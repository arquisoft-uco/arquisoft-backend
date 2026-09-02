package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private final AppLogger logger;
    private final HandlerExceptionResolver resolver;

    // Constructor explicito en lugar de @RequiredArgsConstructor: Lombok no propaga
    // el @Qualifier al parametro generado y Spring inyectaria el resolver equivocado.
    public SecurityAccessDeniedHandler(
            AppLogger logger,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.logger = logger;
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        logger.warn(IniciarSesionKey.LOG_ACCESS_DENIED, request.getRequestURI(), accessDeniedException.getMessage());
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
