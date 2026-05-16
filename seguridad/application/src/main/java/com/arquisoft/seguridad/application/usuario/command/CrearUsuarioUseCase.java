package com.arquisoft.seguridad.application.usuario.command;

import com.arquisoft.seguridad.domain.model.UsuarioRole;

import java.util.UUID;

/**
 * Puerto de entrada — caso de uso para crear un nuevo usuario en el sistema.
 *
 * <p>El use case orquesta: crear el aggregate, persistirlo y drenar los eventos
 * acumulados por el aggregate hacia el bus de mensajería.
 */
public interface CrearUsuarioUseCase {

    /**
     * Crea un nuevo usuario con el email y rol dados.
     *
     * @param email email único del usuario
     * @param rol   rol asignado al usuario
     * @return UUID del usuario recién creado
     */
    UUID crear(String email, UsuarioRole rol);
}
