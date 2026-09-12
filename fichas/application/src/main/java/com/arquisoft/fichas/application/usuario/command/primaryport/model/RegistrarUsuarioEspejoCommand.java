package com.arquisoft.fichas.application.usuario.command.primaryport.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.UUID;

public record RegistrarUsuarioEspejoCommand(
        UUID usuarioId,
        String email,
        String rol
) {

    public RegistrarUsuarioEspejoCommand {
        email = UtilTexto.aplicarTrim(email);
        rol = UtilTexto.aplicarTrim(rol);
    }
}
