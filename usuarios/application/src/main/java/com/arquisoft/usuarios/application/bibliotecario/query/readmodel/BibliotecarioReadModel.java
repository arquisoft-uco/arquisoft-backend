package com.arquisoft.usuarios.application.bibliotecario.query.readmodel;

import java.util.UUID;

public record BibliotecarioReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
