package com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.mapper.UsuarioJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioSolicitudesCommandOutputAdapter implements UsuarioOutputPort {

    private final UsuarioSolicitudesCommandRepository usuarioCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<UsuarioEntity> buscarPorId(UUID id) {
        return usuarioCommandRepository.findById(id).map(UsuarioJpaMapper::toEntity);
    }

    @Override
    public void guardar(UsuarioEntity usuario) {
        usuarioCommandRepository.save(UsuarioJpaMapper.toJpaEntity(usuario));
        logger.debug(UsuarioReplicaKey.LOG_REPLICA_GUARDADA, usuario.id());
    }
}
