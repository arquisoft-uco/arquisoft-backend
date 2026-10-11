package com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria;

import java.util.UUID;

public record EvaluacionFichaPerfilEstudianteCriteria(
        UUID fichaPerfil,
        UUID estudiante
) {
}
