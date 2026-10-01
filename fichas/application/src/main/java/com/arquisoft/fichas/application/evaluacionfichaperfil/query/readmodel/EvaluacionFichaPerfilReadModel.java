package com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilReadModel(
        UUID id,
        UUID fichaPerfilId,
        Instant fechaCreacion,
        String estadoEvaluacion,
        String estadoEvaluacionNombre
) {
}
