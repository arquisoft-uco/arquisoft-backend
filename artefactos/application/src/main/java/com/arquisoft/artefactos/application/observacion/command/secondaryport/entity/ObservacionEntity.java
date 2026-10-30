package com.arquisoft.artefactos.application.observacion.command.secondaryport.entity;

import java.util.UUID;

public record ObservacionEntity(UUID id, UUID revisionAsesor, String descripcion, String estado) {
}
