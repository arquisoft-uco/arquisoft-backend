package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.administrador;

import java.time.Instant;

public record AdministradorRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
