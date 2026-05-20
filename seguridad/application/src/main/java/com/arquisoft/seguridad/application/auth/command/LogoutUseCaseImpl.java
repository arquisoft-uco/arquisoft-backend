package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.dto.LogoutRequestDTO;
import com.arquisoft.seguridad.application.auth.port.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    @Override
    public void ejecutar(LogoutRequestDTO request) {
        tokenBlacklistPort.invalidarToken(request.jti(), request.ttlSegundos());
    }
}
