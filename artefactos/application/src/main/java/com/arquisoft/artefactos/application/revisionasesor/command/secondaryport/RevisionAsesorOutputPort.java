package com.arquisoft.artefactos.application.revisionasesor.command.secondaryport;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface RevisionAsesorOutputPort {

    Optional<RevisionAsesorEntity> obtenerPorId(UUID id);

    boolean existeParticipacion(UUID revisionAsesor, UUID estudiante);

    void actualizarEstado(UUID revisionAsesor, String estado);
}
