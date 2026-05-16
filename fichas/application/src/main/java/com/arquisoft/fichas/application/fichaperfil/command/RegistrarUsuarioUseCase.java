package com.arquisoft.fichas.application.fichaperfil.command;

import java.util.UUID;

/**
 * Puerto de entrada — registra en el contexto {@code fichas} la existencia de un usuario
 * recién creado en el contexto {@code seguridad}.
 *
 * <p>Este puerto desacopla al consumer AMQP de la lógica de negocio de fichas.
 * La implementación inicial solo deja traza de auditoría; la persistencia en tabla
 * espejo se implementará en una historia de usuario posterior.
 */
public interface RegistrarUsuarioUseCase {

    /**
     * Registra (o sincroniza) un usuario en el contexto de fichas.
     *
     * @param usuarioId identificador del usuario en el contexto origen (seguridad)
     * @param email     email del usuario
     * @param rol       código del rol (ej. "estudiante", "asesor")
     */
    void registrar(UUID usuarioId, String email, String rol);
}
