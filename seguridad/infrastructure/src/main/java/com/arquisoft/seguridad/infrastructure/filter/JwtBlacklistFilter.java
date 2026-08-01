package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import com.arquisoft.shared.message.SeguridadMessages;
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

    private final TokenBlacklistOutputPort tokenBlacklistPort;
    private final ObjectMapper objectMapper;

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
                    if (tokenBlacklistPort.estaInvalidado(jti)) {
                        // log.warn: error de cliente — token revocado (detalle interno, no se expone al cliente)
                        log.warn(SeguridadMessages.JwtBlacklist.TOKEN_REVOCADO_LOG,
                                jti, request.getRequestURI());
                        writeErrorResponse(response, request,
                                HttpStatus.UNAUTHORIZED,
                                SeguridadMessages.JwtBlacklist.HTTP_401_ERROR,
                                SeguridadMessages.JwtBlacklist.HTTP_401_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    // log.error: error de servidor — Redis no disponible
                    log.error(SeguridadMessages.JwtBlacklist.REDIS_NO_DISPONIBLE_LOG,
                            e.getMessage(), e);
                    writeErrorResponse(response, request,
                            HttpStatus.SERVICE_UNAVAILABLE,
                            SeguridadMessages.JwtBlacklist.HTTP_503_ERROR,
                            SeguridadMessages.JwtBlacklist.HTTP_503_MESSAGE);
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
