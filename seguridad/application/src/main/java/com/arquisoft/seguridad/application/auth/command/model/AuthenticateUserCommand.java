package com.arquisoft.seguridad.application.auth.command.model;

/**
 * Comando para el caso de uso de autenticacion de usuario.
 *
 * <p>Encapsula las credenciales primitivas que necesita el puerto de entrada,
 * permitiendo que {@code AuthenticateUserInputPort} extienda la interfaz genérica
 * {@code InputPort<AuthenticateUserCommand, AuthResult>}.
 *
 * @param email    correo electronico del usuario
 * @param password contrasena del usuario
 */
public record AuthenticateUserCommand(
        String email,
        String password
) {}
