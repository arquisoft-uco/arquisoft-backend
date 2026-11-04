package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.usuario;

import java.time.Instant;

public record UsuarioModificadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
