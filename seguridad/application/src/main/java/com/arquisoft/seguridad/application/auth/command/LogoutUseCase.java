package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.dto.LogoutRequestDTO;
import com.arquisoft.seguridad.application.auth.port.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutInputPort {

    private final TokenBlacklistOutputPort tokenBlacklistOutputPort;

    @Override
    public void ejecutar(LogoutRequestDTO request) {
        tokenBlacklistOutputPort.invalidarToken(request.jti(), request.ttlSegundos());
    }
}
