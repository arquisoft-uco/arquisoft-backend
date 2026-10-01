package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Token. */
public enum TokenKey implements ClaveMensaje {

    ERROR_REFRESCAR("seguridad.dominio.token.error.refrescar", 0),
    ERROR_REFRESH_INVALIDO_EXPIRADO("seguridad.dominio.token.error.refresh-invalido-expirado", 0),
    LOG_VALIDAR_DEBUG("seguridad.aplicacion.token.log.validar-debug", 0),
    LOG_VALIDAR_RESULTADO("seguridad.aplicacion.token.log.validar-resultado", 1),
    MENSAJE_VALIDO("seguridad.aplicacion.token.mensaje.valido", 0),
    MENSAJE_INVALIDO("seguridad.aplicacion.token.mensaje.invalido", 0),
    LOG_VALIDACION_FALLIDA("seguridad.aplicacion.token.log.validacion-fallida", 1),
    LOG_REFRESH_DEBUG("seguridad.aplicacion.token.log.refresh-debug", 0),
    LOG_REFRESH_EXITOSO("seguridad.aplicacion.token.log.refresh-exitoso", 0),
    LOG_REFRESH_INVALIDO("seguridad.infraestructura.token.log.refresh-invalido", 0),
    LOG_ERROR_REFRESCO_KEYCLOAK("seguridad.infraestructura.token.log.error-refresco-keycloak", 2),
    LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO("seguridad.infraestructura.token.log.keycloak-no-disponible-refresco", 1),
    LOG_INVALIDO_HANDLER("seguridad.infraestructura.token.log.invalido-handler", 3);

    private final String clave;
    private final int parametros;

    TokenKey(String clave, int parametros) {
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
