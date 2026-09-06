package com.arquisoft.fichas.application.itemfichaperfil.query.criteria;

import java.util.UUID;

public record ItemFichaPerfilEstudianteCriteria(
        UUID fichaPerfil,
        UUID estudiante
) {
}
