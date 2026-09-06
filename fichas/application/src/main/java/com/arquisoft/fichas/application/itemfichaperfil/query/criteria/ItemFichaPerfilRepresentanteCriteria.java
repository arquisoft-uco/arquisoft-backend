package com.arquisoft.fichas.application.itemfichaperfil.query.criteria;

import java.util.UUID;

public record ItemFichaPerfilRepresentanteCriteria(
        UUID fichaPerfil,
        UUID representanteComite
) {
}
