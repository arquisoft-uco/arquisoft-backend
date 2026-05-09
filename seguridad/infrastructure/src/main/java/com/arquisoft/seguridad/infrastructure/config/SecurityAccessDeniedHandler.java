package com.arquisoft.seguridad.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Handler de acceso denegado que delega al {@link HandlerExceptionResolver} de Spring MVC,
 * garantizando que las respuestas 403 generadas a nivel de filtro de Spring Security
 * (reglas URL-based en {@code authorizeHttpRequests}) usen el mismo formato
 * {@code ErrorResponseDTO} que los rechazos de {@code @PreAuthorize}.
 *
 * <p>Se inyecta el resolver compuesto con {@code @Qualifier("handlerExceptionResolver")}
 * en lugar de {@code ObjectMapper} para evitar dependencias en la fase de inicialización
 * del contexto de seguridad.</p>
 */
@Slf4j
@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        log.warn("Access denied (filter-level) in {}: {}", request.getRequestURI(), accessDeniedException.getMessage());
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
