package com.arquisoft.shared.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ValidationResult {

    private static final String FORMATO_ERROR = "[%s] %s";
    private static final String SEPARADOR_ERRORES = " | ";

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

    public String describirErrores() {
        return errores.stream()
                .map(e -> FORMATO_ERROR.formatted(e.codigoError(), e.mensaje()))
                .collect(Collectors.joining(SEPARADOR_ERRORES));
    }

    public void lanzarSiTieneErrores() {
        if (tieneErrores()) {
            throw new DomainValidationException(this);
        }
    }

    public void lanzarSiTieneErroresDeEntrada() {
        if (tieneErrores()) {
            throw new ApplicationValidationException(this);
        }
    }
}
