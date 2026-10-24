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
public class AccesoDenegadoHandler implements AccessDeniedHandler {

    private final AppLogger logger;
    private final HandlerExceptionResolver resolvedorExcepciones;

    // Constructor explicito en lugar de @RequiredArgsConstructor: Lombok no propaga
    // el @Qualifier al parametro generado y Spring inyectaria el resolver equivocado.
    public AccesoDenegadoHandler(
            AppLogger logger,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.logger = logger;
        this.resolvedorExcepciones = resolver;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        logger.warn(IniciarSesionKey.LOG_ACCESS_DENIED, request.getRequestURI(), accessDeniedException.getMessage());
        resolvedorExcepciones.resolveException(request, response, null, accessDeniedException);
    }
}
