package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.entity.ArchivoVersionEntity;

import java.util.Optional;
import java.util.UUID;

public interface VersionRepositorioArtefactoAccesoQueryOutputPort {

    Optional<ArchivoVersionEntity> obtenerArchivoPorId(UUID version);
}
