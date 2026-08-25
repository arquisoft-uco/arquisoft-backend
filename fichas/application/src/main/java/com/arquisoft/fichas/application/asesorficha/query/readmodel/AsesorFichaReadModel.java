package com.arquisoft.fichas.application.asesorficha.query.readmodel;

import java.util.UUID;

public record AsesorFichaReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email
) {
}
