package com.arquisoft.seguridad.application.auth.query.port.out;

import com.arquisoft.seguridad.application.auth.query.model.TokenInfoDTO;

/**
 * Puerto de salida para validacion y extraccion de informacion de tokens JWT.
 * Exclusivo del lado query — nunca usado desde paquetes command.
 */
public interface TokenQueryOutputPort {

    /**
     * Extrae la informacion del usuario desde el JWT.
     *
     * @param token el token JWT
     * @return informacion tipada del token
     */
    TokenInfoDTO extractUserInfo(String token);

    /**
     * Valida que el token sea valido y no este expirado.
     *
     * @param token el token JWT
     * @return true si el token es valido
     */
    boolean validateToken(String token);
}
