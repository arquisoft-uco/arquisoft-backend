package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.administrador;

import java.time.Instant;

public record AdministradorAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
