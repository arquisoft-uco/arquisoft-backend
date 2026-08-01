package com.arquisoft.fichas.application.usuario.command.model;

import java.util.UUID;

public record RegistrarUsuarioCommand(
        UUID usuarioId,
        String email,
        String rol
) {}
