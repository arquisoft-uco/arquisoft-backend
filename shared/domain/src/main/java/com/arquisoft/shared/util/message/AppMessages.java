package com.arquisoft.shared.util.message;

/**
 * Plantillas de mensajes para los errores de validación de dominio.
 *
 * <p>Centraliza los textos usados por {@code DomainValidator} y por las
 * validaciones propias de cada contexto acotado, evitando literales dispersos
 * en el código. Cada constante es una plantilla compatible con
 * {@link String#formatted(Object...)} — los marcadores {@code %s} y {@code %d}
 * representan el nombre del campo y el límite numérico respectivamente.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class AppMessages {

    private AppMessages() {}

    public static final class DomainValidator {

        private DomainValidator() {}

        // -------------------------------------------------------------------------
        // Presencia
        // -------------------------------------------------------------------------

        /**
         * El campo '%s' no puede ser nulo.
         */
        public static final String NOT_NULL = "El campo '%s' no puede ser nulo.";

        /**
         * El campo '%s' no puede ser nulo ni vacío.
         */
        public static final String NOT_BLANK = "El campo '%s' no puede ser nulo ni vacío.";

        // -------------------------------------------------------------------------
        // Longitud
        // -------------------------------------------------------------------------

        /**
         * El campo '%s' no puede superar %d caracteres.
         */
        public static final String MAX_LENGTH = "El campo '%s' no puede superar %d caracteres.";

        /**
         * El campo '%s' debe tener al menos %d caracteres.
         */
        public static final String MIN_LENGTH = "El campo '%s' debe tener al menos %d caracteres.";

        // -------------------------------------------------------------------------
        // Formato
        // -------------------------------------------------------------------------

        /**
         * El campo '%s' no tiene formato de correo electrónico válido.
         */
        public static final String VALID_EMAIL = "El campo '%s' no tiene formato de correo electrónico válido.";
    }

    public static final class PaginationRequest {

        private PaginationRequest() {}

        public static final String MENSAJE_PAGE_MAYOR_CERO = "El número de página debe ser >= 0";

        public static final String PAGE_INVALIDA = "PAGINACION_PAGE_INVALIDA";

        public static final String SIZE_INVALIDA = "PAGINACION_SIZE_INVALIDA";

        public static final String MENSAJE_SIZE_MAYOR_CERO = "El tamaño de página debe ser > 0";
    }
}
