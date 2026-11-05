package com.arquisoft.seguridad.application.autenticacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.result.mapper.ResolucionSesionResultMapper;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.ResolucionSesionOutputPort;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.ResolverIdentidadSesionUseCase;
import com.arquisoft.seguridad.domain.autenticacion.CredencialSesionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResolverIdentidadSesionUseCaseImpl implements ResolverIdentidadSesionUseCase {

    private final ResolucionSesionOutputPort resolucionSesionOutputPort;
    private final AppLogger logger;

    @Override
    public ResolucionSesionResult ejecutar(CredencialSesionDomain entrada) {
        logger.debug(ResolucionSesionKey.LOG_RESOLVIENDO);

        var resolucion = resolucionSesionOutputPort.resolver(entrada.getCredencial());
        var resultado = ResolucionSesionResultMapper.toResult(resolucion);

        logger.debug(ResolucionSesionKey.LOG_RESULTADO, resultado instanceof ResolucionSesionResult.Resuelta);

        return resultado;
    }
}
