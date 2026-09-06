package com.arquisoft.solicitudes.application.usuario.command.primaryport.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.UUID;

public record RegistrarUsuarioCommand(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email
) {
    public RegistrarUsuarioCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }
}
