package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp;

public record UsuarioAgregadoPayload(
        String idEvento,
        String usuarioId,
        String identificador,
        String nombre,
        String email,
        String estado,
        String rol
) {
}
