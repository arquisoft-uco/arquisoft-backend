package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Token. */
public enum TokenKey implements MessageKey {

    ERROR_VALOR_REQUERIDO("seguridad.dominio.token.error.valor-requerido"),
    ERROR_REFRESCAR("seguridad.dominio.token.error.refrescar"),
    ERROR_REFRESH_INVALIDO_EXPIRADO("seguridad.dominio.token.error.refresh-invalido-expirado"),
    ERROR_REFRESCAR_DETALLE("seguridad.dominio.token.error.refrescar-detalle"),
    ERROR_INESPERADO_REFRESCO("seguridad.dominio.token.error.inesperado-refresco"),
    ERROR_INVALIDO_DETALLE("seguridad.dominio.token.error.invalido-detalle"),
    ERROR_VALIDAR_DETALLE("seguridad.dominio.token.error.validar-detalle"),
    LOG_VALIDAR_DEBUG("seguridad.aplicacion.token.log.validar-debug"),
    LOG_VALIDO("seguridad.aplicacion.token.log.valido"),
    LOG_INVALIDO("seguridad.aplicacion.token.log.invalido"),
    LOG_VALIDACION_FALLIDA("seguridad.aplicacion.token.log.validacion-fallida"),
    LOG_REFRESH_DEBUG("seguridad.aplicacion.token.log.refresh-debug"),
    LOG_REFRESH_EXITOSO("seguridad.aplicacion.token.log.refresh-exitoso"),
    LOG_REFRESH_INVALIDO("seguridad.infraestructura.token.log.refresh-invalido"),
    LOG_ERROR_REFRESCO_KEYCLOAK("seguridad.infraestructura.token.log.error-refresco-keycloak"),
    LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO("seguridad.infraestructura.token.log.keycloak-no-disponible-refresco"),
    LOG_ERROR_INESPERADO_REFRESCO("seguridad.infraestructura.token.log.error-inesperado-refresco"),
    LOG_VALIDACION_REFRESH_FALLIDA("seguridad.infraestructura.token.log.validacion-refresh-fallida"),
    LOG_ERROR_EXTRAER_INFO("seguridad.infraestructura.token.log.error-extraer-info"),
    LOG_INVALIDO_HANDLER("seguridad.infraestructura.token.log.invalido-handler");

    private final String clave;

    TokenKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.SEGURIDAD;
    }
}
