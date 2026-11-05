package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.TokenInvalidadoOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.IdentidadAutenticada;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AplicadorListaNegra {

    private final AppLogger logger;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final TokenInvalidadoOutputPort tokenInvalidadoPort;
    private final ObjectMapper objectMapper;

    public boolean aplicar(HttpServletRequest request, HttpServletResponse response, IdentidadAutenticada identidad) {
        var idToken = identidad.idToken();
        var emisorConListaNegra = propiedadesAutenticacion.porId(identidad.emisor())
                .map(AutenticacionProperties.Emisor::blacklist)
                .orElse(false);
        if (!emisorConListaNegra || UtilObjeto.esNulo(idToken)) {
            return true;
        }
        var revocado = false;
        try {
            revocado = tokenInvalidadoPort.estaInvalidado(idToken);
        } catch (RuntimeException e) {
            // Fail open acotado: sin Redis no se puede saber si el token fue revocado, y
            // rechazar dejaria la API inutilizable. La entrada de la lista negra caduca con
            // el propio token (5-15 min), asi que la ventana es la vida restante de este.
            logger.error(TokenInvalidadoKey.LOG_REDIS_NO_DISPONIBLE, e, e.getMessage());
        }
        if (revocado) {
            logger.warn(TokenInvalidadoKey.LOG_TOKEN_REVOCADO, idToken, request.getRequestURI());
            escribirRespuesta401(response, request);
            return false;
        }
        return true;
    }

    private void escribirRespuesta401(HttpServletResponse response, HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            objectMapper.writeValue(response.getWriter(),
                    ErrorResponseDTO.builder()
                            .error(Mensajes.obtener(TokenInvalidadoKey.ERROR_HTTP_401))
                            .message(Mensajes.obtener(TokenInvalidadoKey.MENSAJE_HTTP_401))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .path(request.getRequestURI())
                            .build());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
