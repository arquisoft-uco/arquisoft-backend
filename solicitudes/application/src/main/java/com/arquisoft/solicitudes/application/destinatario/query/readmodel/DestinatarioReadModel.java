package com.arquisoft.solicitudes.application.destinatario.query.readmodel;

import java.util.UUID;

public record DestinatarioReadModel(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email
) {
}
