package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.entity.ArchivoVersionArtefactoEntity;

import java.util.Optional;
import java.util.UUID;

public interface VersionArtefactoAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorPorId(UUID versionArtefacto);

    Optional<ArchivoVersionArtefactoEntity> obtenerArchivoPorId(UUID versionArtefacto);

    boolean existeParticipacion(UUID versionArtefacto, UUID estudiante);
}
