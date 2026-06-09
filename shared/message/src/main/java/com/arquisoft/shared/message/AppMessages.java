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
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Paginación
    // ─────────────────────────────────────────────────────────────────────────

    public static final class PaginationRequest {

        private PaginationRequest() {}

        // Mensajes
        public static final String MENSAJE_PAGE_MAYOR_CERO = "El número de página debe ser >= 0";
        public static final String MENSAJE_SIZE_MAYOR_CERO = "El tamaño de página debe ser > 0";

        // Códigos de error
        public static final String PAGE_INVALIDA = "PAGINACION_PAGE_INVALIDA";
        public static final String SIZE_INVALIDA = "PAGINACION_SIZE_INVALIDA";
    }
}
