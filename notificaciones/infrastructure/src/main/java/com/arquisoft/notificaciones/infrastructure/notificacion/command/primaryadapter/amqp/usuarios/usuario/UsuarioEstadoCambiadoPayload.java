package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.usuarios.usuario;

import java.time.Instant;

public record UsuarioEstadoCambiadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String nombre,
        String email,
        String estado,
        String estadoNombre) {
}
