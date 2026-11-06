package com.arquisoft.seguridad.application.autenticacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autenticacion.command.finder.UsuarioIdPorCorreoFinder;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.result.mapper.ResolucionSesionResultMapper;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.ResolucionSesionOutputPort;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.ResolverIdentidadSesionUseCase;
import com.arquisoft.seguridad.domain.autenticacion.CredencialSesionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ResolverIdentidadSesionUseCaseImpl implements ResolverIdentidadSesionUseCase {

    private final ResolucionSesionOutputPort resolucionSesionOutputPort;
    private final UsuarioIdPorCorreoFinder usuarioIdPorCorreoFinder;
    private final AppLogger logger;

    @Override
    public ResolucionSesionResult ejecutar(CredencialSesionDomain entrada) {
        logger.debug(ResolucionSesionKey.LOG_RESOLVIENDO);

        var resolucion = resolucionSesionOutputPort.resolver(entrada.getCredencial());
        var resultado = switch (resolucion) {
            case ResolucionSesion.Resuelta resuelta -> vincularUsuario(resuelta);
            case ResolucionSesion.SinSesion ignorada -> ResolucionSesionResultMapper.toResultSinSesion();
        };

        logger.debug(ResolucionSesionKey.LOG_RESULTADO, resultado instanceof ResolucionSesionResult.Resuelta);

        return resultado;
    }

    private ResolucionSesionResult vincularUsuario(ResolucionSesion.Resuelta resuelta) {
        var correo = UtilTexto.aplicarTrim(resuelta.correo()).toLowerCase(Locale.ROOT);
        var usuario = usuarioIdPorCorreoFinder.obtener(correo);
        var vinculado = !UtilUUID.esPorDefecto(usuario);

        logger.debug(ResolucionSesionKey.LOG_USUARIO_VINCULADO, vinculado);

        return vinculado
                ? ResolucionSesionResultMapper.toResultResuelta(resuelta, usuario)
                : ResolucionSesionResultMapper.toResultUsuarioNoRegistrado();
    }
}
