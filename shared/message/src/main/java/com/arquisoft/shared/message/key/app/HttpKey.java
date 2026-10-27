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

    // Mensajes — campo "message" de ErrorResponseDTO
    MENSAJE_VALIDACION_DOMINIO("app.infraestructura.http.mensaje.validacion-dominio", 1),
    MENSAJE_NO_AUTENTICADO("app.infraestructura.http.mensaje.no-autenticado", 0),
    MENSAJE_SIN_PERMISOS("app.infraestructura.http.mensaje.sin-permisos", 0),
    MENSAJE_INTERNO("app.infraestructura.http.mensaje.interno", 0),
    MENSAJE_RECURSO_NO_EXISTE("app.infraestructura.http.mensaje.recurso-no-existe", 0),
    MENSAJE_METODO_NO_PERMITIDO("app.infraestructura.http.mensaje.metodo-no-permitido", 0),
    MENSAJE_FORMATO_NO_PRODUCIBLE("app.infraestructura.http.mensaje.formato-no-producible", 0),
    MENSAJE_CONTENT_TYPE_NO_SOPORTADO("app.infraestructura.http.mensaje.content-type-no-soportado", 0),
    MENSAJE_ARCHIVO_DEMASIADO_GRANDE("app.infraestructura.http.mensaje.archivo-demasiado-grande", 0),
    MENSAJE_PETICION("app.infraestructura.http.mensaje.peticion", 0),
    MENSAJE_VALIDACION_DATOS("app.infraestructura.http.mensaje.validacion-datos", 0),
    MENSAJE_PARAMETRO_FALTANTE("app.infraestructura.http.mensaje.parametro-faltante", 0),
    MENSAJE_CUERPO_MAL_FORMADO("app.infraestructura.http.mensaje.cuerpo-mal-formado", 0),
    MENSAJE_PETICION_INVALIDA("app.infraestructura.http.mensaje.peticion-invalida", 0),
    MENSAJE_CAMPO_FORMATO_INVALIDO("app.infraestructura.http.mensaje.campo-formato-invalido", 0),

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
