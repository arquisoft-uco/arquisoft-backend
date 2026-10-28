package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record VersionArtefactoEntity(UUID id, UUID artefacto, int numeroVersion, Instant fechaCreacion, String uriArchivo) {
}
