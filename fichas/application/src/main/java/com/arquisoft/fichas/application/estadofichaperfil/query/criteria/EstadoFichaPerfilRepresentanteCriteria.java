package com.arquisoft.fichas.application.estadofichaperfil.query.criteria;

import java.util.UUID;

public record EstadoFichaPerfilRepresentanteCriteria(
        UUID fichaPerfil,
        UUID representanteComite
) {
}
