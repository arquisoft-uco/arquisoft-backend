package com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity;

import java.util.UUID;

public record ObservacionItemEntity(UUID id, UUID revisionItem, String observacion, String estadoObservacionRevision) {
}
