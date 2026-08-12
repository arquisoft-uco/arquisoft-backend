package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.CerrarSesionUseCase;
import com.arquisoft.seguridad.domain.auth.SesionDomain;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CerrarSesionUseCaseImpl implements CerrarSesionUseCase {

    private final TokenInvalidadoOutputPort tokenInvalidadoOutputPort;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(TokenSesionCommand entrada) {
        SesionDomain sesion = SesionDomain.cerrar(
                entrada.identificadorToken(), entrada.tiempoVidaRestante());

        tokenInvalidadoOutputPort.invalidarToken(
                sesion.identificadorToken(), sesion.tiempoVidaRestante());

        log.info(catalogo.obtener(SesionKey.LOG_LOGOUT_EXITOSO),
                sesion.identificadorToken(), sesion.tiempoVidaRestante());
    }
}
