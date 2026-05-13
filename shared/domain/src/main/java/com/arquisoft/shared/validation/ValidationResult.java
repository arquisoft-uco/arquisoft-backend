package com.arquisoft.shared.validation;

import com.arquisoft.shared.exceptions.DomainValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Acumulador de errores de validación de dominio (Notification Pattern).
 *
 * <p>Cada regla de {@link DomainValidator} agrega un {@link ValidationError} aquí
 * en lugar de lanzar inmediatamente. Al finalizar la construcción de la entidad,
 * se invoca {@link #throwIfHasErrors()} para lanzar un único
 * {@link DomainValidationException} con la lista completa de errores.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class ValidationResult {

    /**
     * Error individual de validación: campo afectado, código de error y mensaje descriptivo.
     */
    public record ValidationError(String field, String errorCode, String message) {}

    private final List<ValidationError> errors = new ArrayList<>();

    /**
     * Agrega un error de validación para el campo indicado.
     */
    public void addError(String field, String errorCode, String message) {
        errors.add(new ValidationError(field, errorCode, message));
    }

    /**
     * Retorna {@code true} si se acumuló al menos un error.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Retorna {@code true} si se acumuló al menos un error para el campo indicado.
     * Útil en setters para decidir si asignar o no el valor.
     */
    public boolean hasFieldErrors(String field) {
        return errors.stream().anyMatch(e -> e.field().equals(field));
    }

    /**
     * Retorna la lista inmutable de errores acumulados.
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Lanza {@link DomainValidationException} con todos los errores acumulados
     * si existe al menos uno. No hace nada si no hay errores.
     */
    public void throwIfHasErrors() {
        if (hasErrors()) {
            throw new DomainValidationException(this);
        }
    }
}
