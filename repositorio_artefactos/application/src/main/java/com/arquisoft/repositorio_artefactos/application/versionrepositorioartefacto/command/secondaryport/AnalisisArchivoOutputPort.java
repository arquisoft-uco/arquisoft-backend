package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.AnalisisArchivoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AnalisisArchivoOutputPort {

    Optional<AnalisisArchivoEntity> analizarCarga(UUID carga);
}
