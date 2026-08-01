package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.LogoutUseCase;
import com.arquisoft.shared.message.SeguridadMessages;
import com.arquisoft.seguridad.domain.auth.aggregate.SesionAggregate;
import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklistOutputPort tokenBlacklistOutputPort;

    @Override
    public void ejecutar(TokenSesionCommand entrada) {
        SesionAggregate sesion = SesionAggregate.cerrar(
                entrada.identificadorToken(), entrada.tiempoVidaRestante());

        tokenBlacklistOutputPort.invalidarToken(
                sesion.identificadorToken(), sesion.tiempoVidaRestante());

        log.info(SeguridadMessages.Sesion.LOGOUT_EXITOSO,
                sesion.identificadorToken(), sesion.tiempoVidaRestante());
    }
}
