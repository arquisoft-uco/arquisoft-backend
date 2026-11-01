package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.mapper.RevisionAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
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

    @Override
    public void registrar(RevisionAsesorEntity revision) {
        revisionAsesorCommandRepository.save(RevisionAsesorJpaMapper.toJpaEntity(revision));
        logger.debug(RevisionAsesorKey.LOG_GUARDADA, revision.id());
    }

    @Override
    public boolean existeAbiertaPorVersionArtefacto(UUID versionArtefacto) {
        return revisionAsesorCommandRepository.existsByVersionArtefactoAndEstadoNot(
                versionArtefacto, EstadoRevisionAsesorPersistencia.CERRADA.getCodigo());
    }

    @Override
    public Optional<RevisionAsesorEntity> obtenerMasRecienteDeUltimaVersion(UUID artefacto) {
        return revisionAsesorCommandRepository.buscarMasRecienteDeUltimaVersion(artefacto)
                .map(RevisionAsesorJpaMapper::toEntity);
    }

    @Override
    public boolean existeVersionPosterior(UUID revisionAsesor) {
        return revisionAsesorCommandRepository.existeVersionPosterior(revisionAsesor);
    }

    @Override
    public Optional<RevisionAsesorEntity> obtenerBloqueadaPorObservacion(UUID observacion) {
        return revisionAsesorCommandRepository.buscarBloqueadaPorObservacion(observacion)
                .map(RevisionAsesorJpaMapper::toEntity);
    }

    @Override
    public void eliminar(UUID revisionAsesor) {
        revisionAsesorCommandRepository.deleteById(revisionAsesor);
        logger.debug(RevisionAsesorKey.LOG_ELIMINADA, revisionAsesor);
    }

    @Override
    public Optional<RevisionAsesorEntity> obtenerBloqueadaDeVersionSiguientePorObservacion(UUID observacion) {
        return revisionAsesorCommandRepository
                .buscarBloqueadaDeVersionSiguientePorObservacion(observacion, Limit.of(1))
                .stream()
                .findFirst()
                .map(RevisionAsesorJpaMapper::toEntity);
    }
}
