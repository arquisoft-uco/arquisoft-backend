package com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.mapper.DestinatarioJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DestinatarioCommandOutputAdapter implements DestinatarioOutputPort {

    private final DestinatarioCommandRepository destinatarioCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<UUID> buscarIdPorUsuario(UUID usuarioId) {
        return destinatarioCommandRepository.findByUsuarioId(usuarioId).map(DestinatarioJpaEntity::getId);
    }

    @Override
    public void registrar(DestinatarioEntity destinatario) {
        destinatarioCommandRepository.save(DestinatarioJpaMapper.toJpaEntity(destinatario));
        logger.debug(Mensajes.obtener(SolicitudKey.LOG_GUARDADA), destinatario.id());
    }
}
