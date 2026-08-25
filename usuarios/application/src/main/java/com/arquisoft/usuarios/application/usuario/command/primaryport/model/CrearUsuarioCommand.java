package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

public record CrearUsuarioCommand(
        String email,
        UsuarioRole rol
) {}
