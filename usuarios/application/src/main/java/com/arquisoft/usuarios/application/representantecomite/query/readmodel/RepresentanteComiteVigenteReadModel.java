package com.arquisoft.usuarios.application.representantecomite.query.readmodel;

import java.util.UUID;

public record RepresentanteComiteVigenteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado
) {
}
