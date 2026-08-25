package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.mapper.ValidacionTokenResultMapper;
import com.arquisoft.seguridad.application.auth.command.secondaryport.ValidacionTokenOutputPort;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidarTokenUseCaseImpl implements ValidarTokenUseCase {

    private final ValidacionTokenOutputPort validacionTokenOutputPort;
    private final AppLogger logger;

    @Override
    public ValidacionTokenResult ejecutar(TokenDomain entrada) {
        logger.debug(Mensajes.obtener(TokenKey.LOG_VALIDAR_DEBUG));

        return validacionTokenOutputPort.extraerIdentidad(entrada.getValor())
                .map(ValidacionTokenResultMapper::toResult)
                .orElseGet(ValidacionTokenResultMapper::toResultInvalido);
    }
}
