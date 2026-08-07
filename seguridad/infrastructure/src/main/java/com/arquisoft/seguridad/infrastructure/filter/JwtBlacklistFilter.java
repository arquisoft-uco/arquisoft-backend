package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.domain.auth.port.out.TokenInvalidadoOutputPort;
import com.arquisoft.shared.util.UtilObject;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenInvalidadoOutputPort tokenInvalidadoPort;
    private final ObjectMapper objectMapper;
    private final CatalogoMensajes catalogo;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/swagger-ui")
                || uri.startsWith("/api/v3/api-docs")
                || uri.startsWith("/api/swagger-resources")
                || uri.startsWith("/api/actuator")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/refresh")
                || uri.equals("/api/auth/validate");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = (Jwt) jwtAuth.getCredentials();
            String jti = jwt.getId();

            if (!UtilObject.isNull(jti)) {
                try {
                    if (tokenInvalidadoPort.estaInvalidado(jti)) {
                        // log.warn: error de cliente — token revocado (detalle interno, no se expone al cliente)
                        log.warn(catalogo.obtener(TokenInvalidadoKey.LOG_TOKEN_REVOCADO),
                                jti, request.getRequestURI());
                        writeErrorResponse(response, request,
                                HttpStatus.UNAUTHORIZED,
                                catalogo.obtener(TokenInvalidadoKey.ERROR_HTTP_401),
                                catalogo.obtener(TokenInvalidadoKey.ERROR_HTTP_401_DETALLE));
                        return;
                    }
                } catch (Exception e) {
                    // log.error: error de servidor — Redis no disponible
                    log.error(catalogo.obtener(TokenInvalidadoKey.LOG_REDIS_NO_DISPONIBLE),
                            e.getMessage(), e);
                    writeErrorResponse(response, request,
                            HttpStatus.SERVICE_UNAVAILABLE,
                            catalogo.obtener(TokenInvalidadoKey.ERROR_HTTP_503),
                            catalogo.obtener(TokenInvalidadoKey.ERROR_HTTP_503_DETALLE));
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    HttpServletRequest request,
                                    HttpStatus status,
                                    String error,
                                    String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ErrorResponseDTO.builder()
                        .error(error)
                        .message(message)
                        .status(status.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
