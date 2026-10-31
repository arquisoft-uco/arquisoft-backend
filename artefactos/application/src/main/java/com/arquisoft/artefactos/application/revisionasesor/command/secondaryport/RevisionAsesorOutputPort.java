package com.arquisoft.artefactos.application.revisionasesor.command.secondaryport;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface RevisionAsesorOutputPort {

    Optional<RevisionAsesorEntity> obtenerPorId(UUID id);

    boolean existeParticipacion(UUID revisionAsesor, UUID estudiante);

    void actualizarEstado(UUID revisionAsesor, String estado);

    void registrar(RevisionAsesorEntity revision);

    boolean existeAbiertaPorVersionArtefacto(UUID versionArtefacto);

    Optional<RevisionAsesorEntity> obtenerMasRecienteDeUltimaVersion(UUID artefacto);

    boolean existeVersionPosterior(UUID revisionAsesor);

    Optional<RevisionAsesorEntity> obtenerBloqueadaPorObservacion(UUID observacion);

    Optional<RevisionAsesorEntity> obtenerBloqueadaDeVersionSiguientePorObservacion(UUID observacion);
}
