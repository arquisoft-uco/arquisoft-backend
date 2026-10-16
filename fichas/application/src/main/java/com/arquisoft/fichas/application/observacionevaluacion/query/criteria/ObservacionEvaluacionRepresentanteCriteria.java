package com.arquisoft.fichas.application.observacionevaluacion.query.criteria;

import java.util.UUID;

public record ObservacionEvaluacionRepresentanteCriteria(
        UUID evaluacionFichaPerfil,
        UUID representanteComite
) {
}
