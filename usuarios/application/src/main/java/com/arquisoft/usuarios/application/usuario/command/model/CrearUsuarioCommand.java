package com.arquisoft.usuarios.application.usuario.command.model;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

/**
 * Comando para el caso de uso de creacion de usuario.
 *
 * @param email correo electronico único del usuario
 * @param rol   rol asignado al usuario
 */
public record CrearUsuarioCommand(
        String email,
        UsuarioRole rol
) {}
