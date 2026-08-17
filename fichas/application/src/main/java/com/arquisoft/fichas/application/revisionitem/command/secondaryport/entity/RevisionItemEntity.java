package com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record RevisionItemEntity(UUID id, UUID itemId, String estadoRevisionId, LocalDateTime fechaCreacion) {
}
