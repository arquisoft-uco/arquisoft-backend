package com.arquisoft.fichas.application.itemfichaperfil.query.criteria;

import java.util.UUID;

public record ItemFichaPerfilAsesorCriteria(
        UUID fichaPerfil,
        UUID asesorFicha
) {
}
