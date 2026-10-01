package com.arquisoft.shared.tracing.domain.traza.model;

public enum OrigenTraza {

    HTTP,
    EVENTO,
    PROGRAMADO;

    public String getId() {
        return name();
    }
}
