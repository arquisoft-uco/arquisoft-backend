package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.AuthenticateUserUseCase;
import com.arquisoft.shared.message.SeguridadMessages;
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

    @Override
    public AuthResult ejecutar(AuthenticateUserCommand entrada) {
        log.debug(SeguridadMessages.Autenticacion.AUTENTICAR_DEBUG);

        CredencialesSesion credenciales = authenticationOutputPort.autenticar(entrada.email(), entrada.password());

        log.info(SeguridadMessages.Autenticacion.AUTENTICAR_EXITOSO);

        return new AuthResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
