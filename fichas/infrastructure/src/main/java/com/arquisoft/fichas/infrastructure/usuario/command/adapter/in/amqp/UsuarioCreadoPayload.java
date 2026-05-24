package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

public record UsuarioCreadoPayload(
        String eventId,
        String aggregateId,
        String email,
        String rol
) {
}
