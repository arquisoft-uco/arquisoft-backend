package com.arquisoft.shared.validation;

import com.arquisoft.shared.utils.UtilObject;
import com.arquisoft.shared.utils.UtilText;
import com.arquisoft.shared.utils.messages.AppMessages;

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
 * juntos como {@link com.arquisoft.shared.exceptions.DomainValidationException}.</p>
 *
 * <p>Los mensajes de error se centralizan en {@link AppMessages} y las
 * operaciones de texto en {@link UtilText} — no se permiten literales ni
 * expresiones de normalización inline en este guard.</p>
 *
 * <p>No tiene dependencias de Spring ni Jakarta — Java puro.</p>
 */
public final class DomainValidator {

    private DomainValidator() {}

    /**
     * Acumula error si {@code value} es {@code null}.
     */
    public static void notNull(Object value, String fieldName, String errorCode, ValidationResult result) {
        if (UtilObject.isNull(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.NOT_NULL.formatted(fieldName));
        }
    }

    /**
     * Acumula error si {@code value} es {@code null} o está vacío/en blanco.
     */
    public static void notBlank(String value, String fieldName, String errorCode, ValidationResult result) {
        if (UtilText.isEmptyOrNull(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.NOT_BLANK.formatted(fieldName));
        }
    }

    /**
     * Acumula error si la longitud de {@code value} (sin espacios extremos) supera {@code max}.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void maxLength(String value, int max, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilText.isEmptyOrNull(value) && UtilText.applyTrim(value).length() > max) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.MAX_LENGTH.formatted(fieldName, max));
        }
    }

    /**
     * Acumula error si la longitud de {@code value} (sin espacios extremos) es menor que {@code min}.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void minLength(String value, int min, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilText.isEmptyOrNull(value) && UtilText.applyTrim(value).length() < min) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.MIN_LENGTH.formatted(fieldName, min));
        }
    }

    /**
     * Acumula error si {@code value} no tiene formato de correo electrónico válido.
     * No acumula si {@code value} es {@code null} — combinar con {@link #notBlank} cuando sea necesario.
     */
    public static void validEmail(String value, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilText.isEmptyOrNull(value) && !UtilText.emailStringIsValid(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.VALID_EMAIL.formatted(fieldName));
        }
    }
}
