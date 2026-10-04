package com.arquisoft.usuarios.application.representantecomite.query.readmodel;

import java.util.UUID;

public record RepresentanteComiteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
