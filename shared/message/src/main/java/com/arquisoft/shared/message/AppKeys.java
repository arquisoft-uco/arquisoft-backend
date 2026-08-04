package com.arquisoft.shared.message;

/**
 * Claves del catálogo para los mensajes transversales — bundle {@code messages/app.properties}.
 *
 * <p>Se consumen con {@link MessageCatalog#obtener(String)} o
 * {@link MessageCatalog#formatear(String, Object...)}. Mantener las claves como constantes evita
 * literales sueltos en el código y permite que {@code MessageCatalogClavesTest} verifique que
 * toda clave declarada tiene texto y que todo texto tiene quien lo use.
 */
public final class AppKeys {

    private AppKeys() {}

    /** Mensajes que produce {@code DomainValidator} al acumular errores de integridad. */
    public static final class Validador {

        private Validador() {}

        public static final String NO_NULO = "app.dominio.validador.error.no-nulo";
        public static final String NO_EN_BLANCO = "app.dominio.validador.error.no-en-blanco";
        public static final String LONGITUD_MAXIMA = "app.dominio.validador.error.longitud-maxima";
        public static final String LONGITUD_MINIMA = "app.dominio.validador.error.longitud-minima";
        public static final String CORREO_INVALIDO = "app.dominio.validador.error.correo-invalido";
        public static final String UUID_INVALIDO = "app.dominio.validador.error.uuid-invalido";
        public static final String COLECCION_VACIA = "app.dominio.validador.error.coleccion-vacia";
        public static final String TAMANIO_MAXIMO = "app.dominio.validador.error.tamanio-maximo";
        public static final String SIN_DUPLICADOS = "app.dominio.validador.error.sin-duplicados";
    }

    /** Títulos y detalles de las respuestas de error HTTP que arma {@code GlobalAppExceptionHandler}. */
    public static final class Http {

        private Http() {}

        // Títulos — campo "error" de ErrorResponseDTO
        public static final String ERROR_DOMINIO = "app.infraestructura.http.error.dominio";
        public static final String ERROR_APLICACION = "app.infraestructura.http.error.aplicacion";
        public static final String ACCESO_DENEGADO = "app.infraestructura.http.error.acceso-denegado";
        public static final String SERVICIO_NO_DISPONIBLE = "app.infraestructura.http.error.servicio-no-disponible";
        public static final String ERROR_INTERNO = "app.infraestructura.http.error.interno";
        public static final String ERROR_VALIDACION_DOMINIO = "app.infraestructura.http.error.validacion-dominio";
        public static final String NO_AUTORIZADO = "app.infraestructura.http.error.no-autorizado";
        public static final String PROHIBIDO = "app.infraestructura.http.error.prohibido";
        public static final String PARAMETROS_INVALIDOS = "app.infraestructura.http.error.parametros-invalidos";
        public static final String ERROR_INTERNO_SERVIDOR = "app.infraestructura.http.error.interno-servidor";

        // Detalles — campo "message" de ErrorResponseDTO
        public static final String VALIDACION_DOMINIO_DETALLE = "app.infraestructura.http.error.validacion-dominio-detalle";
        public static final String NO_AUTENTICADO_DETALLE = "app.infraestructura.http.error.no-autenticado-detalle";
        public static final String SIN_PERMISOS_DETALLE = "app.infraestructura.http.error.sin-permisos-detalle";
        public static final String ERROR_INTERNO_DETALLE = "app.infraestructura.http.error.interno-detalle";
        public static final String RECURSO_NO_EXISTE_DETALLE = "app.infraestructura.http.error.recurso-no-existe-detalle";
        public static final String METODO_NO_PERMITIDO_DETALLE = "app.infraestructura.http.error.metodo-no-permitido-detalle";
        public static final String FORMATO_NO_PRODUCIBLE_DETALLE = "app.infraestructura.http.error.formato-no-producible-detalle";
        public static final String CONTENT_TYPE_NO_SOPORTADO_DETALLE = "app.infraestructura.http.error.content-type-no-soportado-detalle";
        public static final String ARCHIVO_DEMASIADO_GRANDE_DETALLE = "app.infraestructura.http.error.archivo-demasiado-grande-detalle";
        public static final String ERROR_PETICION_DETALLE = "app.infraestructura.http.error.peticion-detalle";
        public static final String VALIDACION_DATOS_DETALLE = "app.infraestructura.http.error.validacion-datos-detalle";
        public static final String PARAMETRO_FALTANTE_DETALLE = "app.infraestructura.http.error.parametro-faltante-detalle";
        public static final String CUERPO_MAL_FORMADO_DETALLE = "app.infraestructura.http.error.cuerpo-mal-formado-detalle";
        public static final String PETICION_INVALIDA_DETALLE = "app.infraestructura.http.error.peticion-invalida-detalle";
        public static final String CAMPO_FORMATO_INVALIDO_DETALLE = "app.infraestructura.http.error.campo-formato-invalido-detalle";
        public static final String UUID_FORMATO_INVALIDO_DETALLE = "app.infraestructura.http.error.uuid-formato-invalido-detalle";
    }

    /** Paginación de las consultas. */
    public static final class Paginacion {

        private Paginacion() {}

        public static final String SIZE_MAYOR_CERO = "app.aplicacion.paginacion.error.size-mayor-cero";
    }

    /** Envío de notificaciones — transversal, lo produce {@code shared:notification}. */
    public static final class Notificacion {

        private Notificacion() {}

        public static final String ERROR_ENVIO_FALLIDO = "app.infraestructura.notificacion.error.envio-fallido";
    }
}
