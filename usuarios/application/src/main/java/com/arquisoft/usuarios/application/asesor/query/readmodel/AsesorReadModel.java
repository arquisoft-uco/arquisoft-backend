package com.arquisoft.usuarios.application.asesor.query.readmodel;

import java.util.UUID;

public record AsesorReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
