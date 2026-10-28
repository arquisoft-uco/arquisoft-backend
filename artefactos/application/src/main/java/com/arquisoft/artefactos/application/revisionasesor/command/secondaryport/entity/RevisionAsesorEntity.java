package com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity;

import java.util.UUID;

public record RevisionAsesorEntity(UUID id, UUID versionArtefacto, String estado) {
}
