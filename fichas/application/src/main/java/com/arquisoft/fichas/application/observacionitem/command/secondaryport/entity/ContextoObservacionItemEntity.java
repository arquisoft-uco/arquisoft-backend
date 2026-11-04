package com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity;

import java.util.UUID;

public record ContextoObservacionItemEntity(UUID revisionItem, String estadoRevision, UUID fichaPerfil, UUID asesorFicha) {
}
