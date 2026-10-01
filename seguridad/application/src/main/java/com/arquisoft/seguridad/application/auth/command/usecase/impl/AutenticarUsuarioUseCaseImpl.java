package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.mapper.AutenticacionResultMapper;
import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
import com.arquisoft.seguridad.application.auth.command.usecase.AutenticarUsuarioUseCase;
import com.arquisoft.seguridad.domain.auth.AutenticacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final AutenticacionOutputPort autenticacionOutputPort;
    private final AppLogger logger;

    @Override
    public AutenticacionResult ejecutar(AutenticacionDomain entrada) {
        logger.debug(AutenticacionKey.LOG_AUTENTICAR_DEBUG);

        var credenciales = autenticacionOutputPort.autenticar(
                entrada.getCorreo(), entrada.getClaveAcceso());

        logger.info(AutenticacionKey.LOG_AUTENTICAR_EXITOSO);

        return AutenticacionResultMapper.toResult(credenciales);
    }
}
