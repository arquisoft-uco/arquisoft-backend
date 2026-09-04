package com.arquisoft.fichas.application.itemfichaperfil.query.criteria;

import java.util.UUID;

public record ItemFichaPerfilCriteria(
        UUID fichaPerfil,
        UUID asesorFicha
) {
}
