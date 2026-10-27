package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.mapper.RevisionAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionAsesorCommandOutputAdapter implements RevisionAsesorOutputPort {

    private final RevisionAsesorCommandRepository revisionAsesorCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<RevisionAsesorEntity> obtenerPorId(UUID id) {
        return revisionAsesorCommandRepository.buscarBloqueadaPorId(id).map(RevisionAsesorJpaMapper::toEntity);
    }

    @Override
    public boolean existeParticipacion(UUID revisionAsesor, UUID estudiante) {
        return revisionAsesorCommandRepository.existeParticipacion(revisionAsesor, estudiante);
    }

    @Override
    public void actualizarEstado(UUID revisionAsesor, String estado) {
        revisionAsesorCommandRepository.actualizarEstado(revisionAsesor, estado);
        logger.debug(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_ACTUALIZADO, revisionAsesor);
    }
}
