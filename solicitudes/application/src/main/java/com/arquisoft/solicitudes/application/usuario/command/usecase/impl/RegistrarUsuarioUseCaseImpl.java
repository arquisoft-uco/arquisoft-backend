package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.result.mapper.AgregacionUsuarioResultMapper;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.solicitudes.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionUsuarioResult ejecutar(UsuarioDomain usuario) {
        var vigente = usuarioPorIdFinder.obtener(usuario.getId());
        logger.debug(UsuarioReplicaKey.LOG_VERIFICACION_AGREGAR, usuario.getId(), vigente.isPresent());

        if (vigente.isPresent()) {
            if (!usuario.getOcurridoEn().isAfter(vigente.get().ocurridoEn())) {
                return AgregacionUsuarioResultMapper.toResultDescartada(usuario, vigente.get().ocurridoEn());
            }
            return AgregacionUsuarioResultMapper.toResultDuplicada(usuario);
        }

        usuarioOutputPort.guardar(UsuarioMapper.toEntity(usuario));
        return AgregacionUsuarioResultMapper.toResultAgregada(usuario);
    }
}
