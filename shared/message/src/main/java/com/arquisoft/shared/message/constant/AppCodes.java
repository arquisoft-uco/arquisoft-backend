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

    public static final class Paginacion {

        private Paginacion() {}

        public static final String SIZE_INVALIDA = "PAGINACION_SIZE_INVALIDA";
    }

    /** Envio de notificaciones — transversal, lo produce {@code shared:notification}. */
    public static final class Notificacion {

        private Notificacion() {}

        public static final String ENVIO_FALLIDO = "NOTIFICACION_ENVIO_FALLIDO";
    }
}
