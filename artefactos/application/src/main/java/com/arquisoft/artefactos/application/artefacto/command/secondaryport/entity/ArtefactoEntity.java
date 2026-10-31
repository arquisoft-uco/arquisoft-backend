package com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoEntity(
        UUID id, UUID proyectoGrado, UUID versionRepositorioArtefacto, UUID asesor, Instant omitidoEn) {
}
