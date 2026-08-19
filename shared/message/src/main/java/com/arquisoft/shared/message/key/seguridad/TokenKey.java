package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Token. */
public enum TokenKey implements ClaveMensaje {

    ERROR_VALOR_REQUERIDO("seguridad.dominio.token.error.valor-requerido", 0),
    ERROR_REFRESCAR("seguridad.dominio.token.error.refrescar", 0),
    ERROR_REFRESH_INVALIDO_EXPIRADO("seguridad.dominio.token.error.refresh-invalido-expirado", 0),
    ERROR_REFRESCAR_DETALLE("seguridad.dominio.token.error.refrescar-detalle", 1),
    ERROR_INESPERADO_REFRESCO("seguridad.dominio.token.error.inesperado-refresco", 1),
    ERROR_INVALIDO_DETALLE("seguridad.dominio.token.error.invalido-detalle", 1),
    ERROR_VALIDAR_DETALLE("seguridad.dominio.token.error.validar-detalle", 1),
    LOG_VALIDAR_DEBUG("seguridad.aplicacion.token.log.validar-debug", 0),
    LOG_VALIDO("seguridad.aplicacion.token.log.valido", 0),
    LOG_INVALIDO("seguridad.aplicacion.token.log.invalido", 0),
    LOG_VALIDACION_FALLIDA("seguridad.aplicacion.token.log.validacion-fallida", 0),
    LOG_REFRESH_DEBUG("seguridad.aplicacion.token.log.refresh-debug", 0),
    LOG_REFRESH_EXITOSO("seguridad.aplicacion.token.log.refresh-exitoso", 0),
    LOG_REFRESH_INVALIDO("seguridad.infraestructura.token.log.refresh-invalido", 0),
    LOG_ERROR_REFRESCO_KEYCLOAK("seguridad.infraestructura.token.log.error-refresco-keycloak", 0),
    LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO("seguridad.infraestructura.token.log.keycloak-no-disponible-refresco", 0),
    LOG_ERROR_INESPERADO_REFRESCO("seguridad.infraestructura.token.log.error-inesperado-refresco", 0),
    LOG_VALIDACION_REFRESH_FALLIDA("seguridad.infraestructura.token.log.validacion-refresh-fallida", 0),
    LOG_ERROR_EXTRAER_INFO("seguridad.infraestructura.token.log.error-extraer-info", 0),
    LOG_INVALIDO_HANDLER("seguridad.infraestructura.token.log.invalido-handler", 0);

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
