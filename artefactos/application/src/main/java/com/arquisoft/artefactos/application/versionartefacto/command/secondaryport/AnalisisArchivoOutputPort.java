package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.AnalisisArchivoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AnalisisArchivoOutputPort {

    Optional<AnalisisArchivoEntity> analizarCarga(UUID carga);
}
