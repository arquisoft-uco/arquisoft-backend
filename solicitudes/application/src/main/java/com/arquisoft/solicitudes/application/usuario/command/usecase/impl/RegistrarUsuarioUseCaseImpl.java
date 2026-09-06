package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
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
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistrarUsuarioCommand entrada) {
        var usuario = UsuarioDomain.crear(
                entrada.usuarioId(), entrada.identificador(), entrada.nombre(), entrada.email());
        var entity = UsuarioMapper.toEntity(usuario);

        if (usuarioOutputPort.existePorId(usuario.getId())) {
            usuarioOutputPort.actualizar(entity);
        } else {
            usuarioOutputPort.registrar(entity);
        }

        logger.debug(UsuarioReplicaKey.LOG_REPLICA_GUARDADA, usuario.getId());
    }
}
