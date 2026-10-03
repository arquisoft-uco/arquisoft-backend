package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.primaryadapter.amqp.usuarios.bibliotecario;

import java.time.Instant;

public record BibliotecarioAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
