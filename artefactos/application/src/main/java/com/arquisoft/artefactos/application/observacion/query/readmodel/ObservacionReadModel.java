package com.arquisoft.artefactos.application.observacion.query.readmodel;

import java.util.UUID;

public record ObservacionReadModel(
        UUID id,
        UUID revisionAsesor,
        String descripcion,
        String estado,
        String estadoNombre
) {
}
