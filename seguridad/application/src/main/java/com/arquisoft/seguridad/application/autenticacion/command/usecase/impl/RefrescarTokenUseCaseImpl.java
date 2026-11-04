package com.arquisoft.seguridad.application.autenticacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autenticacion.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.autenticacion.command.result.mapper.RefrescoTokenResultMapper;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.AutenticacionOutputPort;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.RefrescarTokenUseCase;
import com.arquisoft.seguridad.domain.autenticacion.TokenDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefrescarTokenUseCaseImpl implements RefrescarTokenUseCase {

    private final AutenticacionOutputPort autenticacionOutputPort;
    private final AppLogger logger;

    @Override
    public RefrescoTokenResult ejecutar(TokenDomain entrada) {
        logger.debug(TokenKey.LOG_REFRESH_DEBUG);

        var credenciales = autenticacionOutputPort.refrescar(entrada.getValor());

        logger.info(TokenKey.LOG_REFRESH_EXITOSO);

        return RefrescoTokenResultMapper.toResult(credenciales);
    }
}
