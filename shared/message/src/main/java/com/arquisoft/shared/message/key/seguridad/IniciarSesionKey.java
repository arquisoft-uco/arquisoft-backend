package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Login. */
public enum IniciarSesionKey implements ClaveMensaje {

    ERROR_AUTENTICAR_KEYCLOAK("seguridad.dominio.login.error.autenticar-keycloak"),
    ERROR_CREDENCIALES_INVALIDAS("seguridad.dominio.login.error.credenciales-invalidas"),
    ERROR_COMUNICACION_KEYCLOAK("seguridad.dominio.login.error.comunicacion-keycloak"),
    ERROR_SERVICIO_NO_DISPONIBLE("seguridad.dominio.login.error.servicio-no-disponible"),
    ERROR_INESPERADO_AUTENTICACION("seguridad.dominio.login.error.inesperado-autenticacion"),
    ERROR_HTTP_401("seguridad.infraestructura.login.error.http-401"),
    LOG_CREDENCIALES_INVALIDAS("seguridad.infraestructura.login.log.credenciales-invalidas"),
    LOG_ERROR_AUTENTICACION_KEYCLOAK("seguridad.infraestructura.login.log.error-autenticacion-keycloak"),
    LOG_KEYCLOAK_NO_DISPONIBLE("seguridad.infraestructura.login.log.keycloak-no-disponible"),
    LOG_ERROR_INESPERADO("seguridad.infraestructura.login.log.error-inesperado"),
    LOG_CREDENCIALES_INVALIDAS_HANDLER("seguridad.infraestructura.login.log.credenciales-invalidas-handler"),
    LOG_EXCEPCION_AUTENTICACION("seguridad.infraestructura.login.log.excepcion-autenticacion"),
    LOG_ACCESS_DENIED("seguridad.infraestructura.login.log.access-denied"),
    LOG_UNAUTHORIZED("seguridad.infraestructura.login.log.unauthorized"),
    LOG_JWT_DECODER_CONFIG("seguridad.infraestructura.login.log.jwt-decoder-config");

    private final String clave;

    IniciarSesionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.SEGURIDAD;
    }
}
