package com.arquisoft.usuarios.application.asesorficha.query.readmodel;

import java.util.UUID;

public record AsesorFichaReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
