package com.arquisoft.shared.message.constant;

/**
 * Códigos de error transversales.
 *
 * <p>Los códigos no viven en el bundle a propósito: no son texto para el usuario sino el
 * identificador estable que viaja en {@code ErrorResponseDTO.errorCode} y contra el que asientan
 * los clientes de la API y las pruebas. Sacarlos a un {@code .properties} los volvería
 * resolubles solo en tiempo de ejecución y perdería la verificación del compilador sobre un
 * dato que es contrato, no presentación.
 */
public final class AppCodes {

    private AppCodes() {}

    public static final class Http {

        private Http() {}

        public static final String PARAMETRO_INVALIDO = "PARAMETRO_INVALIDO";
        public static final String ARCHIVO_DEMASIADO_GRANDE = "ARCHIVO_DEMASIADO_GRANDE";
        public static final String CAMPO_FORMATO_INVALIDO = "CAMPO_FORMATO_INVALIDO";
    }

    /** Notification Pattern — los produce {@code shared:validation} al agrupar los fieldErrors. */
    public static final class Validacion {

        private Validacion() {}

        public static final String DOMINIO = "DOMAIN_VALIDATION_ERROR";
        public static final String APLICACION = "APPLICATION_VALIDATION_ERROR";
    }

    public static final class Paginacion {

        private Paginacion() {}

        public static final String SIZE_INVALIDA = "PAGINACION_SIZE_INVALIDA";
    }

    public static final class Consulta {

        private Consulta() {}

        public static final String FILTRO_CONECTOR_INVALIDO = "FILTRO_CONECTOR_INVALIDO";
        public static final String FILTRO_OPERADOR_INVALIDO = "FILTRO_OPERADOR_INVALIDO";
        public static final String CAMPO_ORDEN_NO_PERMITIDO = "CAMPO_ORDEN_NO_PERMITIDO";
        public static final String PROFUNDIDAD_FILTRO_EXCEDIDA = "PROFUNDIDAD_FILTRO_EXCEDIDA";
        public static final String CAMPO_FILTRO_NO_PERMITIDO = "CAMPO_FILTRO_NO_PERMITIDO";
        public static final String VALOR_REQUERIDO = "VALOR_REQUERIDO";
        public static final String OPERADOR_REQUIERE_LISTA = "OPERADOR_REQUIERE_LISTA";
        public static final String OPERADOR_NO_ACEPTA_LISTA = "OPERADOR_NO_ACEPTA_LISTA";
        public static final String SORT_CAMPO_VACIO = "SORT_CAMPO_VACIO";
        public static final String SORT_DIRECTION_INVALIDA = "SORT_DIRECTION_INVALIDA";
        public static final String FILTRO_INVALIDO = "FILTRO_INVALIDO";
        public static final String CONECTOR_REQUERIDO = "CONECTOR_REQUERIDO";
    }

    public static final class Minio {

        private Minio() {}

        public static final String URL_CARGA_FALLIDA = "MINIO_URL_CARGA_FALLIDA";
        public static final String URL_DESCARGA_FALLIDA = "MINIO_URL_DESCARGA_FALLIDA";
        public static final String ELIMINACION_FALLIDA = "MINIO_ELIMINACION_FALLIDA";
        public static final String VERIFICACION_FALLIDA = "MINIO_VERIFICACION_FALLIDA";
    }
}
