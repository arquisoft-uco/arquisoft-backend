package com.arquisoft.shared.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BaseError {

    private final String errorCode;
    private final String message;
    private final List<String> trace;

    private BaseError(String errorCode, String message, List<String> trace) {
        this.errorCode = errorCode;
        this.message = message;
        this.trace = Collections.unmodifiableList(trace);
    }

    public static BaseError of(String errorCode, String message) {
        return new BaseError(errorCode, message, Collections.emptyList());
    }

    public static BaseError of(String errorCode, String message, Throwable cause) {
        return new BaseError(errorCode, message, buildTrace(cause));
    }

    private static List<String> buildTrace(Throwable cause) {
        List<String> trace = new ArrayList<>();
        Throwable current = cause;
        while (current != null) {
            trace.add(current.getClass().getSimpleName() + ": " + current.getMessage());
            current = current.getCause();
        }
        return trace;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getTrace() {
        return trace;
    }
}
