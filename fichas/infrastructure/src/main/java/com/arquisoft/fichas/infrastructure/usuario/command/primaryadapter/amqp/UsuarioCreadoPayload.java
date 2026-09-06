package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

import java.time.Instant;

public record UsuarioCreadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuarioId,
        String email,
        String rol
) {
}
