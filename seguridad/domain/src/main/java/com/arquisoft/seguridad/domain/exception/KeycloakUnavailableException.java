package com.arquisoft.seguridad.domain.exception;

/**
 * Excepción que indica que el servidor de identidad (Keycloak) no está disponible.
 *
 * <p>Se lanza cuando ocurre un fallo de conectividad o timeout de red al intentar
 * comunicarse con Keycloak — situaciones de infraestructura, no de lógica de negocio.
 *
 * <p>Intencionalmente NO extiende {@link AuthenticationException}: un timeout de red
 * no es un fallo de autenticación; es una indisponibilidad del servicio externo.
 * El handler la mapea a 503 Service Unavailable.
 */
public class KeycloakUnavailableException extends RuntimeException {

    public KeycloakUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
