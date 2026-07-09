package com.arquisoft.fichas.domain.estadoevaluacion;

public enum EstadoEvaluacion {

    EN_EVALUACION("En Evaluación"),
    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada Con Observaciones"),
    NO_APROBADA("No Aprobada"),
    DESCARTADA("Descartada");

    private final String id;
    private final String nombre;

    EstadoEvaluacion(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esTerminal() {
        return this == APROBADA || this == NO_APROBADA || this == DESCARTADA || this == APROBADA_CON_OBSERVACIONES;
    }

    public boolean esEnEvaluacion() {
        return this == EN_EVALUACION;
    }
}
