package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.application.auth.command.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.usecase.AutenticarUsuarioUseCase;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AutenticacionOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final AutenticacionOutputPort autenticacionOutputPort;
    private final CatalogoMensajes catalogo;

    @Override
    public AutenticacionResult ejecutar(AutenticarUsuarioCommand entrada) {
        log.debug(catalogo.obtener(AutenticacionKey.LOG_AUTENTICAR_DEBUG));

        CredencialesSesion credenciales = autenticacionOutputPort.autenticar(entrada.email(), entrada.contrasena());

        log.info(catalogo.obtener(AutenticacionKey.LOG_AUTENTICAR_EXITOSO));

        return new AutenticacionResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance()
        );
    }
}
