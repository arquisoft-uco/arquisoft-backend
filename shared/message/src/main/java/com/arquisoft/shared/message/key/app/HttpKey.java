package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Títulos y detalles de las respuestas de error HTTP que arma {@code GlobalAppExceptionHandler}. */
public enum HttpKey implements ClaveMensaje {

    // Títulos — campo "error" de ErrorResponseDTO
    ERROR_DOMINIO("app.infraestructura.http.error.dominio"),
    ERROR_APLICACION("app.infraestructura.http.error.aplicacion"),
    SERVICIO_NO_DISPONIBLE("app.infraestructura.http.error.servicio-no-disponible"),
    ERROR_INTERNO("app.infraestructura.http.error.interno"),
    ERROR_VALIDACION_DOMINIO("app.infraestructura.http.error.validacion-dominio"),
    NO_AUTORIZADO("app.infraestructura.http.error.no-autorizado"),
    PROHIBIDO("app.infraestructura.http.error.prohibido"),
    PARAMETROS_INVALIDOS("app.infraestructura.http.error.parametros-invalidos"),
    ERROR_INTERNO_SERVIDOR("app.infraestructura.http.error.interno-servidor"),

    // Detalles — campo "message" de ErrorResponseDTO
    VALIDACION_DOMINIO_DETALLE("app.infraestructura.http.error.validacion-dominio-detalle"),
    NO_AUTENTICADO_DETALLE("app.infraestructura.http.error.no-autenticado-detalle"),
    SIN_PERMISOS_DETALLE("app.infraestructura.http.error.sin-permisos-detalle"),
    ERROR_INTERNO_DETALLE("app.infraestructura.http.error.interno-detalle"),
    RECURSO_NO_EXISTE_DETALLE("app.infraestructura.http.error.recurso-no-existe-detalle"),
    METODO_NO_PERMITIDO_DETALLE("app.infraestructura.http.error.metodo-no-permitido-detalle"),
    FORMATO_NO_PRODUCIBLE_DETALLE("app.infraestructura.http.error.formato-no-producible-detalle"),
    CONTENT_TYPE_NO_SOPORTADO_DETALLE("app.infraestructura.http.error.content-type-no-soportado-detalle"),
    ARCHIVO_DEMASIADO_GRANDE_DETALLE("app.infraestructura.http.error.archivo-demasiado-grande-detalle"),
    ERROR_PETICION_DETALLE("app.infraestructura.http.error.peticion-detalle"),
    VALIDACION_DATOS_DETALLE("app.infraestructura.http.error.validacion-datos-detalle"),
    PARAMETRO_FALTANTE_DETALLE("app.infraestructura.http.error.parametro-faltante-detalle"),
    CUERPO_MAL_FORMADO_DETALLE("app.infraestructura.http.error.cuerpo-mal-formado-detalle"),
    PETICION_INVALIDA_DETALLE("app.infraestructura.http.error.peticion-invalida-detalle"),
    CAMPO_FORMATO_INVALIDO_DETALLE("app.infraestructura.http.error.campo-formato-invalido-detalle"),
    UUID_FORMATO_INVALIDO_DETALLE("app.infraestructura.http.error.uuid-formato-invalido-detalle"),

    // Logs — GlobalAppExceptionHandler
    LOG_VALIDACION_DOMINIO_FALLIDA("app.infraestructura.http.log.validacion-dominio-fallida"),
    LOG_VALIDACION_APLICACION_FALLIDA("app.infraestructura.http.log.validacion-aplicacion-fallida"),
    LOG_EXCEPCION("app.infraestructura.http.log.excepcion"),
    LOG_AUTENTICACION_FALLIDA("app.infraestructura.http.log.autenticacion-fallida"),
    LOG_ACCESO_DENEGADO("app.infraestructura.http.log.acceso-denegado"),
    LOG_VIOLACION_RESTRICCION("app.infraestructura.http.log.violacion-restriccion"),
    LOG_ERROR_INESPERADO("app.infraestructura.http.log.error-inesperado"),
    LOG_EXCEPCION_SPRING_MVC("app.infraestructura.http.log.excepcion-spring-mvc"),
    LOG_ERROR_VALIDACION_CAMPOS("app.infraestructura.http.log.error-validacion-campos");

    private final String clave;

    HttpKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
