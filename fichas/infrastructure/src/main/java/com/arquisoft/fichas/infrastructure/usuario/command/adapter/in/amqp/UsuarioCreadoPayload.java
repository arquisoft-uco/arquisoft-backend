package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

public record UsuarioCreadoPayload(
        String eventId,
        String usuarioId,
        String email,
        String rol
) {
}
