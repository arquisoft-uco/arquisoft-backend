package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.RefreshTokenUseCase;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final AuthenticationOutputPort authenticationOutputPort;
    private final MessageCatalog catalog;

    @Override
    public RefrescoTokenResult ejecutar(String entrada) {
        log.debug(catalog.obtener(SeguridadKeys.Token.LOG_REFRESH_DEBUG));

        CredencialesSesion credenciales = authenticationOutputPort.refrescar(entrada);

        log.info(catalog.obtener(SeguridadKeys.Token.LOG_REFRESH_EXITOSO));

        return new RefrescoTokenResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
