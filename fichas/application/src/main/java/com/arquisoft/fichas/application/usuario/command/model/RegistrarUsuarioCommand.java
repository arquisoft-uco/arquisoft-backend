package com.arquisoft.fichas.application.usuario.command.model;

import java.util.UUID;

/**
 * Comando para el caso de uso de registro de usuario espejo en el contexto de fichas.
 *
 * <p>Encapsula los datos del evento {@code UsuarioCreadoEvent} recibido desde el
 * contexto de seguridad vía RabbitMQ, permitiendo que {@code RegistrarUsuarioUseCase}
 * extienda la interfaz genérica {@code VoidUseCase<RegistrarUsuarioCommand>}.
 *
 * @param usuarioId identificador UUID del usuario creado en seguridad
 * @param email     correo electronico del usuario
 * @param rol       codigo del rol asignado al usuario
 */
public record RegistrarUsuarioCommand(
        UUID usuarioId,
        String email,
        String rol
) {}
