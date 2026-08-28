package com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.mapper.RemitenteJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemitenteCommandOutputAdapter implements RemitenteOutputPort {

    private final RemitenteCommandRepository remitenteCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<UUID> buscarIdPorUsuario(UUID usuarioId) {
        return remitenteCommandRepository.findByUsuarioId(usuarioId).map(RemitenteJpaEntity::getId);
    }

    @Override
    public void registrar(RemitenteEntity remitente) {
        remitenteCommandRepository.save(RemitenteJpaMapper.toJpaEntity(remitente));
        logger.debug(Mensajes.obtener(SolicitudKey.LOG_GUARDADA), remitente.id());
    }
}
