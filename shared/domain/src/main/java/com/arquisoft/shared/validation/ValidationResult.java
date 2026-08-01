package com.arquisoft.shared.validation;

import com.arquisoft.shared.exception.DomainValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ValidationResult {

    public record ValidationError(String campo, String codigoError, String mensaje) {}

    private final List<ValidationError> errores = new ArrayList<>();

    public void agregarError(String campo, String codigoError, String mensaje) {
        errores.add(new ValidationError(campo, codigoError, mensaje));
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public boolean tieneErroresDeCampo(String campo) {
        return errores.stream().anyMatch(e -> e.campo().equals(campo));
    }

    public List<ValidationError> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public void lanzarSiTieneErrores() {
        if (tieneErrores()) {
            throw new DomainValidationException(this);
        }
    }
}
