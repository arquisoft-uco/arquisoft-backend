package com.arquisoft.seguridad.application.usuario.command.model;

import com.arquisoft.seguridad.domain.usuario.model.UsuarioRole;

/**
 * Comando para el caso de uso de creacion de usuario.
 *
 * <p>Encapsula los parámetros primitivos que necesita el puerto de entrada,
 * permitiendo que {@code CrearUsuarioInputPort} extienda la interfaz genérica
 * {@code InputPort<CrearUsuarioCommand, UUID>}.
 *
 * @param email correo electronico único del usuario
 * @param rol   rol asignado al usuario
 */
public record CrearUsuarioCommand(
        String email,
        UsuarioRole rol
) {}
