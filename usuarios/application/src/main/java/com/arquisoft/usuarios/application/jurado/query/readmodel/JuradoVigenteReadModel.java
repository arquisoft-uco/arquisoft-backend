package com.arquisoft.usuarios.application.jurado.query.readmodel;

import java.util.UUID;

public record JuradoVigenteReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado
) {
}
