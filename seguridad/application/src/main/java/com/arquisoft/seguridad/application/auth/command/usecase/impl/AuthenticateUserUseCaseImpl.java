package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.usecase.AuthenticateUserUseCase;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final AuthenticationOutputPort authenticationOutputPort;
    private final MessageCatalog catalog;

    @Override
    public AutenticacionResult ejecutar(AuthenticateUserCommand entrada) {
        log.debug(catalog.obtener(AutenticacionKey.LOG_AUTENTICAR_DEBUG));

        CredencialesSesion credenciales = authenticationOutputPort.autenticar(entrada.email(), entrada.password());

        log.info(catalog.obtener(AutenticacionKey.LOG_AUTENTICAR_EXITOSO));

        return new AutenticacionResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
