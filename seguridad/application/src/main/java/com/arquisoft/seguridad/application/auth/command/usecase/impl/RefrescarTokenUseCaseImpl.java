package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.secondaryport.AutenticacionOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefrescarTokenUseCaseImpl implements RefrescarTokenUseCase {

    private final AutenticacionOutputPort autenticacionOutputPort;
    private final CatalogoMensajes catalogo;

    @Override
    public RefrescoTokenResult ejecutar(String entrada) {
        log.debug(catalogo.obtener(TokenKey.LOG_REFRESH_DEBUG));

        CredencialesSesion credenciales = autenticacionOutputPort.refrescar(entrada);

        log.info(catalogo.obtener(TokenKey.LOG_REFRESH_EXITOSO));

        return new RefrescoTokenResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
