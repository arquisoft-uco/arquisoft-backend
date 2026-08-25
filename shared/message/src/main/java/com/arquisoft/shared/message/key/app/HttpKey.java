package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

/** Títulos y detalles de las respuestas de error HTTP que arma {@code GlobalAppExceptionHandler}. */
public enum HttpKey implements ClaveMensaje {

    // Títulos — campo "error" de ErrorResponseDTO
    ERROR_DOMINIO("app.infraestructura.http.error.dominio", 0),
    ERROR_APLICACION("app.infraestructura.http.error.aplicacion", 0),
    SERVICIO_NO_DISPONIBLE("app.infraestructura.http.error.servicio-no-disponible", 0),
    ERROR_INTERNO("app.infraestructura.http.error.interno", 0),
    ERROR_VALIDACION_DOMINIO("app.infraestructura.http.error.validacion-dominio", 0),
    NO_AUTORIZADO("app.infraestructura.http.error.no-autorizado", 0),
    PROHIBIDO("app.infraestructura.http.error.prohibido", 0),
    PARAMETROS_INVALIDOS("app.infraestructura.http.error.parametros-invalidos", 0),
    ERROR_INTERNO_SERVIDOR("app.infraestructura.http.error.interno-servidor", 0),

    // Detalles — campo "message" de ErrorResponseDTO
    VALIDACION_DOMINIO_DETALLE("app.infraestructura.http.error.validacion-dominio-detalle", 1),
    NO_AUTENTICADO_DETALLE("app.infraestructura.http.error.no-autenticado-detalle", 0),
    SIN_PERMISOS_DETALLE("app.infraestructura.http.error.sin-permisos-detalle", 0),
    ERROR_INTERNO_DETALLE("app.infraestructura.http.error.interno-detalle", 0),
    RECURSO_NO_EXISTE_DETALLE("app.infraestructura.http.error.recurso-no-existe-detalle", 0),
    METODO_NO_PERMITIDO_DETALLE("app.infraestructura.http.error.metodo-no-permitido-detalle", 0),
    FORMATO_NO_PRODUCIBLE_DETALLE("app.infraestructura.http.error.formato-no-producible-detalle", 0),
    CONTENT_TYPE_NO_SOPORTADO_DETALLE("app.infraestructura.http.error.content-type-no-soportado-detalle", 0),
    ARCHIVO_DEMASIADO_GRANDE_DETALLE("app.infraestructura.http.error.archivo-demasiado-grande-detalle", 0),
    ERROR_PETICION_DETALLE("app.infraestructura.http.error.peticion-detalle", 0),
    VALIDACION_DATOS_DETALLE("app.infraestructura.http.error.validacion-datos-detalle", 0),
    PARAMETRO_FALTANTE_DETALLE("app.infraestructura.http.error.parametro-faltante-detalle", 1),
    CUERPO_MAL_FORMADO_DETALLE("app.infraestructura.http.error.cuerpo-mal-formado-detalle", 0),
    PETICION_INVALIDA_DETALLE("app.infraestructura.http.error.peticion-invalida-detalle", 0),
    CAMPO_FORMATO_INVALIDO_DETALLE("app.infraestructura.http.error.campo-formato-invalido-detalle", 1),
    UUID_FORMATO_INVALIDO_DETALLE("app.infraestructura.http.error.uuid-formato-invalido-detalle", 0),

    // Logs — GlobalAppExceptionHandler
    LOG_VALIDACION_DOMINIO_FALLIDA("app.infraestructura.http.log.validacion-dominio-fallida", 3),
    LOG_VALIDACION_APLICACION_FALLIDA("app.infraestructura.http.log.validacion-aplicacion-fallida", 3),
    LOG_EXCEPCION("app.infraestructura.http.log.excepcion", 4),
    LOG_AUTENTICACION_FALLIDA("app.infraestructura.http.log.autenticacion-fallida", 2),
    LOG_ACCESO_DENEGADO("app.infraestructura.http.log.acceso-denegado", 2),
    LOG_VIOLACION_RESTRICCION("app.infraestructura.http.log.violacion-restriccion", 2),
    LOG_ERROR_INESPERADO("app.infraestructura.http.log.error-inesperado", 2),
    LOG_EXCEPCION_SPRING_MVC("app.infraestructura.http.log.excepcion-spring-mvc", 3),
    LOG_ERROR_VALIDACION_CAMPOS("app.infraestructura.http.log.error-validacion-campos", 1);

    private final String clave;
    private final int parametros;

    HttpKey(String clave, int parametros) {
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
