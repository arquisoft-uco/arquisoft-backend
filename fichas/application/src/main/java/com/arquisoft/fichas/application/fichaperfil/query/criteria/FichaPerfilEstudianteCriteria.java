package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import java.util.UUID;

public record FichaPerfilEstudianteCriteria(
        UUID fichaPerfil,
        UUID estudiante
) {
}
