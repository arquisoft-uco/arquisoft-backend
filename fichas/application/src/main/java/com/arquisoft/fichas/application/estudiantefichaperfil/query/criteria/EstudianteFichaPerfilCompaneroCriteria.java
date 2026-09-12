package com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria;

import java.util.UUID;

public record EstudianteFichaPerfilCompaneroCriteria(
        UUID fichaPerfil,
        UUID estudiante
) {
}
