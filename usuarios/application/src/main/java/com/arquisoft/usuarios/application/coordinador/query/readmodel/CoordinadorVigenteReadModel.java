package com.arquisoft.usuarios.application.coordinador.query.readmodel;

import java.util.UUID;

public record CoordinadorVigenteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto
) {
}
