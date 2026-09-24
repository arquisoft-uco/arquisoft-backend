package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.result.mapper.ActualizacionUsuarioResultMapper;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.solicitudes.application.usuario.command.usecase.ActualizarUsuarioUseCase;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarUsuarioUseCaseImpl implements ActualizarUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionUsuarioResult ejecutar(UsuarioDomain entrada) {
        var vigente = usuarioPorIdFinder.obtener(entrada.getId());
        logger.debug(UsuarioReplicaKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionUsuarioResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionUsuarioResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        usuarioOutputPort.actualizar(UsuarioMapper.toEntity(vigente));
        return ActualizacionUsuarioResultMapper.toResultActualizada(vigente);
    }
}
