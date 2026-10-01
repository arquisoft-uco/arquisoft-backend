package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.Optional;
import java.util.regex.Pattern;

public final class CorrelacionEntrante {

    private static final Pattern SEGURA = Pattern.compile("[A-Za-z0-9\\-]{1,64}");

    private CorrelacionEntrante() {}

    public static Optional<String> validar(final String valor) {
        if (UtilTexto.esVacioONulo(valor) || !SEGURA.matcher(valor).matches()) {
            return Optional.empty();
        }
        return Optional.of(valor);
    }
}
