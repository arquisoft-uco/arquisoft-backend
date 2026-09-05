package com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel;

import java.util.UUID;

public record EstudianteFichaPerfilReadModel(
        UUID id,
        UUID fichaPerfilId,
        UUID estudianteId,
        String nombre,
        String email
) {
}
