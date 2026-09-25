package com.arquisoft.usuarios.application.estudiante.query.readmodel;

import java.util.UUID;

public record EstudianteVigenteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto
) {
}
