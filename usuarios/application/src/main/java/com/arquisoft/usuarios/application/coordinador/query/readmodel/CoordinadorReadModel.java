package com.arquisoft.usuarios.application.coordinador.query.readmodel;

import java.util.UUID;

public record CoordinadorReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
