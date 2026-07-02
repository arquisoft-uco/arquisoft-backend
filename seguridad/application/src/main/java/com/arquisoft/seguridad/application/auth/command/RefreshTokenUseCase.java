package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.port.in.RefreshTokenInputPort;
import com.arquisoft.shared.message.SeguridadMessages;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCase implements RefreshTokenInputPort {

    private final AuthenticationOutputPort authenticationOutputPort;

    @Override
    public RefreshResult ejecutar(String tokenRefresco) {
        log.debug(SeguridadMessages.Token.REFRESH_DEBUG);

        CredencialesSesion credenciales = authenticationOutputPort.refrescar(tokenRefresco);

        log.info(SeguridadMessages.Token.REFRESH_EXITOSO);

        return new RefreshResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
