package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class NoAutenticadoEntryPoint implements AuthenticationEntryPoint {

    private final AppLogger logger;
    private final HandlerExceptionResolver resolvedorExcepciones;

    // Constructor explicito en lugar de @RequiredArgsConstructor: Lombok no propaga
    // el @Qualifier al parametro generado y Spring inyectaria el resolver equivocado.
    public NoAutenticadoEntryPoint(
            AppLogger logger,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.logger = logger;
        this.resolvedorExcepciones = resolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        logger.warn(IniciarSesionKey.LOG_UNAUTHORIZED, request.getRequestURI(), authException.getMessage());
        resolvedorExcepciones.resolveException(request, response, null, authException);
    }
}
