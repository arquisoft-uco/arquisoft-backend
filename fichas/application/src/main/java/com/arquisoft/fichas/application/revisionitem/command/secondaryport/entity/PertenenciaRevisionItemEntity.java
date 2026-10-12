package com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity;

import java.util.UUID;

public record PertenenciaRevisionItemEntity(UUID fichaPerfilId, boolean esPropietario, String estadoRevision) {
}
