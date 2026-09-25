package com.arquisoft.usuarios.application.estudiante.query.readmodel;

import java.util.UUID;

public record EstudianteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
