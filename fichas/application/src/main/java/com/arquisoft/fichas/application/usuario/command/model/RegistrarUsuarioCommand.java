package com.arquisoft.fichas.application.usuario.command.model;

import com.arquisoft.shared.util.UtilText;

import java.util.UUID;

public record RegistrarUsuarioCommand(
        UUID usuarioId,
        String email,
        String rol
) {

    public RegistrarUsuarioCommand {
        email = UtilText.applyTrim(email);
        rol = UtilText.applyTrim(rol);
    }
}
