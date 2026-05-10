package com.arquisoft.shared.validation;

/**
 * Guard central de invariantes de dominio — Notification Pattern.
 *
 * <p>Métodos estáticos reutilizables que evalúan una regla genérica y,
 * si no se cumple, <strong>acumulan</strong> el error en el {@link ValidationResult}
 * recibido <em>sin lanzar excepción</em>. Esto permite que todas las reglas
 * de una entidad se evalúen antes de decidir si la construcción es válida.</p>
 *
 * <p>El lanzamiento ocurre una sola vez al final, mediante
 * {@link ValidationResult#throwIfHasErrors()}, entregando todos los errores
 * juntos como {@link DomainValidationException}.</p>
 *
 * <p>No tiene dependencias de Spring ni Jakarta — Java puro.</p>
 */
public final class DomainValidator {

    private DomainValidator() {}

    /**
     * Acumula error si {@code value} es {@code null}.
     */
    public static void notNull(Object value, String fieldName, String errorCode, ValidationResult result) {
        if (value == null) {
            result.addError(fieldName, errorCode,
                "El campo '%s' no puede ser nulo.".formatted(fieldName));
        }
    }

    /**
     * Acumula error si {@code value} es {@code null} o está vacío/en blanco.
     */
    public static void notBlank(String value, String fieldName, String errorCode, ValidationResult result) {
        if (value == null || value.isBlank()) {
            result.addError(fieldName, errorCode,
                "El campo '%s' no puede ser nulo ni vacío.".formatted(fieldName));
        }
    }

    /**
     * Acumula error si la longitud de {@code value} (sin espacios extremos) supera {@code max}.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void maxLength(String value, int max, String fieldName, String errorCode, ValidationResult result) {
        if (value != null && value.trim().length() > max) {
            result.addError(fieldName, errorCode,
                "El campo '%s' no puede superar %d caracteres.".formatted(fieldName, max));
        }
    }

    /**
     * Acumula error si la longitud de {@code value} (sin espacios extremos) es menor que {@code min}.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void minLength(String value, int min, String fieldName, String errorCode, ValidationResult result) {
        if (value != null && value.trim().length() < min) {
            result.addError(fieldName, errorCode,
                "El campo '%s' debe tener al menos %d caracteres.".formatted(fieldName, min));
        }
    }

    /**
     * Acumula error si {@code value} no tiene formato de correo electrónico válido.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void validEmail(String value, String fieldName, String errorCode, ValidationResult result) {
        if (value != null && !value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            result.addError(fieldName, errorCode,
                "El campo '%s' no tiene formato de correo electrónico válido.".formatted(fieldName));
        }
    }
}
