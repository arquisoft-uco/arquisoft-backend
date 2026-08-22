package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Login. */
public enum IniciarSesionKey implements ClaveMensaje {

    ERROR_AUTENTICAR_KEYCLOAK("seguridad.dominio.login.error.autenticar-keycloak", 0),
    ERROR_CREDENCIALES_INVALIDAS("seguridad.dominio.login.error.credenciales-invalidas", 0),
    ERROR_COMUNICACION_KEYCLOAK("seguridad.dominio.login.error.comunicacion-keycloak", 1),
    ERROR_SERVICIO_NO_DISPONIBLE("seguridad.dominio.login.error.servicio-no-disponible", 0),
    ERROR_HTTP_401("seguridad.infraestructura.login.error.http-401", 0),
    ERROR_AUDIENCIA_INVALIDA("seguridad.infraestructura.login.error.audiencia-invalida", 1),
    LOG_CREDENCIALES_INVALIDAS("seguridad.infraestructura.login.log.credenciales-invalidas", 0),
    LOG_ERROR_AUTENTICACION_KEYCLOAK("seguridad.infraestructura.login.log.error-autenticacion-keycloak", 0),
    LOG_KEYCLOAK_NO_DISPONIBLE("seguridad.infraestructura.login.log.keycloak-no-disponible", 0),
    LOG_CREDENCIALES_INVALIDAS_HANDLER("seguridad.infraestructura.login.log.credenciales-invalidas-handler", 0),
    LOG_EXCEPCION_AUTENTICACION("seguridad.infraestructura.login.log.excepcion-autenticacion", 0),
    LOG_ACCESS_DENIED("seguridad.infraestructura.login.log.access-denied", 0),
    LOG_UNAUTHORIZED("seguridad.infraestructura.login.log.unauthorized", 0),
    LOG_JWT_DECODER_CONFIG("seguridad.infraestructura.login.log.jwt-decoder-config", 0);

    private final String clave;
    private final int parametros;

    IniciarSesionKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
