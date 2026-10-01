package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.seguridad.application.auth.command.usecase.CerrarSesionUseCase;
import com.arquisoft.seguridad.domain.auth.SesionDomain;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CerrarSesionUseCaseImpl implements CerrarSesionUseCase {

    private final TokenInvalidadoOutputPort tokenInvalidadoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(SesionDomain entrada) {
        if (!entrada.requiereInvalidacion()) {
            logger.info(SesionKey.LOG_LOGOUT_TOKEN_EXPIRADO,
                    entrada.getIdentificadorToken());
            return;
        }

        tokenInvalidadoOutputPort.invalidarToken(
                entrada.getIdentificadorToken(), entrada.getTiempoVidaRestante());

        logger.info(SesionKey.LOG_LOGOUT_EXITOSO,
                entrada.getIdentificadorToken(), entrada.getTiempoVidaRestante());
    }
}
