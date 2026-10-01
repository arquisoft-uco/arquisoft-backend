package com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria;

import java.util.UUID;

public record EvaluacionFichaPerfilRepresentanteCriteria(
        UUID fichaPerfil,
        UUID representanteComite
) {
}
