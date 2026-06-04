package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.LogoutInputPort;
import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import com.arquisoft.seguridad.domain.auth.aggregate.SesionAggregate;
import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutInputPort {

    private final TokenBlacklistOutputPort tokenBlacklistOutputPort;

    @Override
    public void ejecutar(TokenSesionCommand command) {
        SesionAggregate sesion = SesionAggregate.cerrar(
                command.identificadorToken(), command.tiempoVidaRestante());

        tokenBlacklistOutputPort.invalidarToken(
                sesion.identificadorToken(), sesion.tiempoVidaRestante());

        log.info(SeguridadApplicationMessages.LogoutUseCase.LOGOUT_EXITOSO,
                sesion.identificadorToken(), sesion.tiempoVidaRestante());
    }
}
