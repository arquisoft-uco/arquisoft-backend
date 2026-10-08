package com.arquisoft.fichas.application.observacionevaluacion.query.criteria;

import java.util.UUID;

public record ObservacionEvaluacionAsesorCriteria(
        UUID evaluacionFichaPerfil,
        UUID asesorFicha
) {
}
