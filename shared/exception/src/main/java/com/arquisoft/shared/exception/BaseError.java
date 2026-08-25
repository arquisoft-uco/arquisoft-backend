package com.arquisoft.shared.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BaseError {

    private final String codigoError;
    private final String message;
    private final List<String> traza;

    private BaseError(String codigoError, String message, List<String> traza) {
        this.codigoError = codigoError;
        this.message = message;
        this.traza = Collections.unmodifiableList(traza);
    }

    public static BaseError of(String codigoError, String message) {
        return new BaseError(codigoError, message, Collections.emptyList());
    }

    public static BaseError of(String codigoError, String message, Throwable cause) {
        return new BaseError(codigoError, message, construirTraza(cause));
    }

    private static List<String> construirTraza(Throwable cause) {
        List<String> traza = new ArrayList<>();
        Throwable current = cause;
        while (current != null) {
            traza.add(current.getClass().getSimpleName() + ": " + current.getMessage());
            current = current.getCause();
        }
        return traza;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getTraza() {
        return traza;
    }
}
