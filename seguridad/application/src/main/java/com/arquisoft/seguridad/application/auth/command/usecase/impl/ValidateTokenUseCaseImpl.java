package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidateTokenUseCase;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenDomain;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.port.out.TokenValidationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

    private final TokenValidationOutputPort tokenValidationOutputPort;
    private final MessageCatalog catalog;

    @Override
    public ValidacionTokenResult ejecutar(TokenDomain entrada) {
        log.debug(catalog.obtener(TokenKey.LOG_VALIDAR_DEBUG));

        try {
            if (tokenValidationOutputPort.validarToken(entrada.valor())) {
                IdentidadToken identidad = tokenValidationOutputPort.extraerInfo(entrada.valor());

                return new ValidacionTokenResult(
                        true,
                        identidad.identidadId(),
                        identidad.correo(),
                        catalog.obtener(TokenKey.LOG_VALIDO)
                );
            } else {
                return new ValidacionTokenResult(false, null, null,
                        catalog.obtener(TokenKey.LOG_INVALIDO));
            }
        } catch (Exception e) {
            log.debug(catalog.obtener(TokenKey.LOG_VALIDACION_FALLIDA), e.getMessage());
            return new ValidacionTokenResult(false, null, null,
                    catalog.formatear(TokenKey.ERROR_VALIDAR_DETALLE, e.getMessage()));
        }
    }
}
