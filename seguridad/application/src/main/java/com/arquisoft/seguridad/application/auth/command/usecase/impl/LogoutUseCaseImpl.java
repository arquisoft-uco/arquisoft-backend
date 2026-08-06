package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.LogoutUseCase;
import com.arquisoft.seguridad.domain.auth.aggregate.SesionDomain;
import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklistOutputPort tokenBlacklistOutputPort;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(TokenSesionCommand entrada) {
        SesionDomain sesion = SesionDomain.cerrar(
                entrada.identificadorToken(), entrada.tiempoVidaRestante());

        tokenBlacklistOutputPort.invalidarToken(
                sesion.identificadorToken(), sesion.tiempoVidaRestante());

        log.info(catalog.obtener(SesionKey.LOG_LOGOUT_EXITOSO),
                sesion.identificadorToken(), sesion.tiempoVidaRestante());
    }
}
