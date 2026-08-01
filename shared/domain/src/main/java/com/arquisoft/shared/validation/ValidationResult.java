package com.arquisoft.shared.validation;

import com.arquisoft.shared.exception.DomainValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ValidationResult {

    public record ValidationError(String field, String errorCode, String message) {}

    private final List<ValidationError> errors = new ArrayList<>();

    public void addError(String field, String errorCode, String message) {
        errors.add(new ValidationError(field, errorCode, message));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasFieldErrors(String field) {
        return errors.stream().anyMatch(e -> e.field().equals(field));
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void throwIfHasErrors() {
        if (hasErrors()) {
            throw new DomainValidationException(this);
        }
    }
}
