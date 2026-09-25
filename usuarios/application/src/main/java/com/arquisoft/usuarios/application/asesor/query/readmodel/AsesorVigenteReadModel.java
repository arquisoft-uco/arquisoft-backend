package com.arquisoft.usuarios.application.asesor.query.readmodel;

import java.util.UUID;

public record AsesorVigenteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado
) {
}
