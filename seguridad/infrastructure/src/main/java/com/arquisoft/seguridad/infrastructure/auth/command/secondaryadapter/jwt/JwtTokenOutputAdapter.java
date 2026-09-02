package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt;

import com.arquisoft.seguridad.application.auth.command.secondaryport.ValidacionTokenOutputPort;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;
import com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt.mapper.JwtIdentidadMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenOutputAdapter implements ValidacionTokenOutputPort {

    private final JwtDecoder jwtDecoder;
    private final AppLogger logger;

    @Override
    public Optional<IdentidadProveedor> extraerIdentidad(String token) {
        try {
            return Optional.of(JwtIdentidadMapper.toModel(jwtDecoder.decode(token)));
        } catch (JwtException e) {
            logger.warn(TokenKey.LOG_VALIDACION_FALLIDA, e.getMessage());
            return Optional.empty();
        }
    }
}
