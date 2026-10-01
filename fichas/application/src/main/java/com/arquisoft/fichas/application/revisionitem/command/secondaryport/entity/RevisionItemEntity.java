package com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record RevisionItemEntity(UUID id, UUID item, String estadoRevision, Instant fechaCreacion) {
}
