package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

public record UsuarioCreadoPayload(
        String idEvento,
        String usuarioId,
        String email,
        String rol
) {
}
