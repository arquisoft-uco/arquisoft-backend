package com.arquisoft.solicitudes.application.remitente.query.readmodel;

import java.util.UUID;

public record RemitenteReadModel(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email
) {
}
