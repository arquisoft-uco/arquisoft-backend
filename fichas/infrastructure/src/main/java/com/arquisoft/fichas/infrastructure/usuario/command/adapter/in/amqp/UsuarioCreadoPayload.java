package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

public record UsuarioCreadoPayload(
        String idEvento,
        String usuarioId,
        String email,
        String rol
) {
}
