package com.arquisoft.fichas.infrastructure.representantecomite.command.primaryadapter.amqp.usuarios.representantecomite;

import java.time.Instant;

public record RepresentanteComiteRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
