package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.application.auth.command.secondaryport.ValidacionTokenOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidarTokenUseCaseImpl implements ValidarTokenUseCase {

    private final ValidacionTokenOutputPort validacionTokenOutputPort;
    private final CatalogoMensajes catalogo;

    @Override
    public ValidacionTokenResult ejecutar(TokenDomain entrada) {
        log.debug(catalogo.obtener(TokenKey.LOG_VALIDAR_DEBUG));

        try {
            if (validacionTokenOutputPort.validarToken(entrada.valor())) {
                IdentidadToken identidad = validacionTokenOutputPort.extraerInfo(entrada.valor());

                return new ValidacionTokenResult(
                        true,
                        identidad.identidadId(),
                        identidad.correo(),
                        catalogo.obtener(TokenKey.LOG_VALIDO)
                );
            } else {
                return new ValidacionTokenResult(false, null, null,
                        catalogo.obtener(TokenKey.LOG_INVALIDO));
            }
        } catch (Exception e) {
            log.debug(catalogo.obtener(TokenKey.LOG_VALIDACION_FALLIDA), e.getMessage());
            return new ValidacionTokenResult(false, null, null,
                    catalogo.formatear(TokenKey.ERROR_VALIDAR_DETALLE, e.getMessage()));
        }
    }
}
