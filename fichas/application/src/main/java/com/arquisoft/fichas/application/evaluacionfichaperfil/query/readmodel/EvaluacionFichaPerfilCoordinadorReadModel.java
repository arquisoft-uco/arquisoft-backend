package com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel;

import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilCoordinadorReadModel(
        UUID id,
        UUID fichaPerfil,
        Instant fechaCreacion,
        String estadoEvaluacion,
        String estadoEvaluacionNombre,
        RepresentanteComiteReadModel representanteComite
) {
}
