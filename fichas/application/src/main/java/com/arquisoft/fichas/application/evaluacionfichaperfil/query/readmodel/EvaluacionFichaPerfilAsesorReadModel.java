package com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel;

import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilAsesorReadModel(
        UUID id,
        UUID fichaPerfil,
        String tituloProyecto,
        Instant fechaCreacion,
        String estadoEvaluacion,
        String estadoEvaluacionNombre,
        RepresentanteComiteReadModel representanteComite
) {
}
