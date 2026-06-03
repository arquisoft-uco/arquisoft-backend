package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.model.LogoutCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.LogoutInputPort;
import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutInputPort {

    private final TokenBlacklistOutputPort tokenBlacklistOutputPort;

    @Override
    public void ejecutar(LogoutCommand command) {
        tokenBlacklistOutputPort.invalidarToken(command.jti(), command.ttlSegundos());
    }
}
