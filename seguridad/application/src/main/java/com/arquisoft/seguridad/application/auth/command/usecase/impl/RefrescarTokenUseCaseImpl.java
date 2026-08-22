package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.mapper.RefrescoTokenResultMapper;
import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
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
        logger.debug(Mensajes.obtener(TokenKey.LOG_REFRESH_DEBUG));

        var credenciales = autenticacionOutputPort.refrescar(entrada.getValor());

        logger.info(Mensajes.obtener(TokenKey.LOG_REFRESH_EXITOSO));

        return RefrescoTokenResultMapper.toResult(credenciales);
    }
}
