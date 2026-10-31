package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface VersionArtefactoAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorPorId(UUID versionArtefacto);

    boolean existeParticipacion(UUID versionArtefacto, UUID estudiante);
}
