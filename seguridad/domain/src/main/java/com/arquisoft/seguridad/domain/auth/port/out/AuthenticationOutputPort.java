package com.arquisoft.seguridad.domain.auth.port.out;

import com.arquisoft.seguridad.domain.auth.model.CredencialesToken;

/**
 * Puerto de salida para autenticacion contra el servidor de identidad (Keycloak).
 * El dominio define el contrato; la infraestructura implementa la comunicacion
 * con el proveedor de identidad externo.
 */
public interface AuthenticationOutputPort {

    /**
     * Autentica al usuario contra el proveedor de identidad usando email y contrasena.
     *
     * @param email    email del usuario
     * @param password contrasena del usuario
     * @return credenciales de token obtenidas del proveedor de identidad
     */
    CredencialesToken authenticate(String email, String password);

    /**
     * Refresca el token de acceso usando el refresh token.
     *
     * @param refreshToken el refresh token
     * @return nuevas credenciales de token
     */
    CredencialesToken refresh(String refreshToken);

    /**
     * Valida que el refresh token sea valido.
     *
     * @param refreshToken el refresh token
     * @return true si es valido
     */
    boolean validateRefreshToken(String refreshToken);
}
