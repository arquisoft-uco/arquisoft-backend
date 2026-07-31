package com.arquisoft.shared.message;

public final class AppMessages {

    private AppMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Validadores de dominio (DomainValidator)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class DomainValidator {

        private DomainValidator() {}

        public static final String NOT_NULL    = "El campo '%s' no puede ser nulo.";
        public static final String NOT_BLANK   = "El campo '%s' no puede ser nulo ni vacío.";
        public static final String MAX_LENGTH  = "El campo '%s' no puede superar %d caracteres.";
        public static final String MIN_LENGTH  = "El campo '%s' debe tener al menos %d caracteres.";
        public static final String VALID_EMAIL = "El campo '%s' no tiene formato de correo electrónico válido.";
        public static final String VALID_UUID  = "El dato '%s' no cumple el formato de identificador único universal.";
        public static final String NOT_EMPTY   = "La colección '%s' no puede ser nula ni estar vacía.";
        public static final String MAX_SIZE    = "La colección '%s' no puede tener más de %d elementos.";
        public static final String SIN_DUPLICADOS = "La colección '%s' contiene el elemento duplicado: %s.";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Capa web transversal (GlobalAppExceptionHandler / ErrorResponseDTO)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Http {

        private Http() {}

        // Títulos de error por familia de excepción
        public static final String ERROR_DOMINIO             = "Error de dominio";
        public static final String ERROR_APLICACION          = "Error de aplicación";
        public static final String ACCESO_DENEGADO           = "Acceso denegado";
        public static final String SERVICIO_NO_DISPONIBLE    = "Servicio no disponible";
        public static final String ERROR_INTERNO             = "Error interno";
        public static final String ERROR_VALIDACION_DOMINIO  = "Error de validación de dominio";
        public static final String NO_AUTORIZADO             = "Unauthorized";
        public static final String PROHIBIDO                 = "Forbidden";
        public static final String PARAMETROS_INVALIDOS      = "Parámetros inválidos";
        public static final String ERROR_INTERNO_SERVIDOR    = "Internal Server Error";

        // Mensajes al cliente
        public static final String VALIDACION_DOMINIO_MSG    = "La entidad contiene %d error(es) de validación.";
        public static final String NO_AUTENTICADO_MSG        = "No autenticado o token inválido";
        public static final String SIN_PERMISOS_MSG          = "No tienes permisos para acceder a este recurso";
        public static final String ERROR_INTERNO_MSG         = "Error interno del servidor";
        public static final String RECURSO_NO_EXISTE_MSG     = "El recurso solicitado no existe";
        public static final String METODO_NO_PERMITIDO_MSG   = "El método HTTP no está permitido en este endpoint";
        public static final String FORMATO_NO_PRODUCIBLE_MSG = "No se puede producir una respuesta en el formato solicitado";
        public static final String CONTENT_TYPE_NO_SOPORTADO_MSG = "Content-Type no soportado";
        public static final String ARCHIVO_DEMASIADO_GRANDE_MSG  = "El archivo supera el tamaño máximo permitido";
        public static final String ERROR_PETICION_MSG        = "Error en la petición";
        public static final String VALIDACION_DATOS_MSG      = "Error de validación en los datos enviados";
        public static final String PARAMETRO_FALTANTE_MSG    = "Parámetro requerido faltante: %s";
        public static final String CUERPO_MAL_FORMADO_MSG    = "Cuerpo de la petición mal formado";
        public static final String PETICION_INVALIDA_MSG     = "Petición inválida";
        public static final String CAMPO_FORMATO_INVALIDO_MSG = "El dato '%s' no cumple el formato esperado.";
        public static final String UUID_FORMATO_INVALIDO_MSG =
                "El dato no cumple el formato de identificador único universal";

        // Códigos de error
        public static final String PARAMETRO_INVALIDO        = "PARAMETRO_INVALIDO";
        public static final String ARCHIVO_DEMASIADO_GRANDE  = "ARCHIVO_DEMASIADO_GRANDE";
        public static final String CAMPO_FORMATO_INVALIDO    = "CAMPO_FORMATO_INVALIDO";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Paginación
    // ─────────────────────────────────────────────────────────────────────────

    public static final class PaginationRequest {

        private PaginationRequest() {}

        // Mensajes
        public static final String MENSAJE_SIZE_MAYOR_CERO = "El tamaño de página debe ser > 0";

        // Códigos de error
        public static final String SIZE_INVALIDA = "PAGINACION_SIZE_INVALIDA";
    }
}
