package com.arquisoft.fichas.application.estadofichaperfil.query.criteria;

import java.util.UUID;

public record EstadoFichaPerfilEstudianteCriteria(
        UUID fichaPerfil,
        UUID estudiante
) {
}
