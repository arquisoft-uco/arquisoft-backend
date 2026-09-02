package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import com.arquisoft.seguridad.infrastructure.config.security.RutasAutenticacion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.filter.RutasTecnicas;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(-100)
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

    // Oculta deliberadamente el campo heredado GenericFilterBean.logger (Commons Logging):
    // el proyecto registra a traves del puerto AppLogger, no del logger del framework.
    private final AppLogger logger;

    private final RutasAutenticacion rutasAutenticacion;
    private final TokenInvalidadoOutputPort tokenInvalidadoPort;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return RutasTecnicas.esRutaTecnica(uri) || rutasAutenticacion.esPublica(uri);
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

            if (!UtilObjeto.esNulo(jti)) {
                try {
                    if (tokenInvalidadoPort.estaInvalidado(jti)) {
                        // logger.warn: error de cliente — token revocado (detalle interno, no se expone al cliente)
                        logger.warn(TokenInvalidadoKey.LOG_TOKEN_REVOCADO,
                                jti, request.getRequestURI());
                        writeErrorResponse(response, request,
                                HttpStatus.UNAUTHORIZED,
                                Mensajes.obtener(TokenInvalidadoKey.ERROR_HTTP_401),
                                Mensajes.obtener(TokenInvalidadoKey.ERROR_HTTP_401_DETALLE));
                        return;
                    }
                } catch (Exception e) {
                    // Fail open acotado: sin Redis no se puede saber si el token fue revocado, y
                    // rechazar dejaria la API inutilizable. La entrada de la lista negra caduca con
                    // el propio token (5-15 min), asi que la ventana es la vida restante de este.
                    logger.error(TokenInvalidadoKey.LOG_REDIS_NO_DISPONIBLE,
                            e, e.getMessage());
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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(),
                ErrorResponseDTO.builder()
                        .error(error)
                        .message(message)
                        .status(status.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
