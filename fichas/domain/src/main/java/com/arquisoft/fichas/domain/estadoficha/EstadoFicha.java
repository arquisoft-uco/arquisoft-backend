package com.arquisoft.fichas.domain.estadoficha;

public enum EstadoFicha {

    EN_CONSTRUCCION("En Construccion"),
    EN_REVISION("En Revision"),
    DISPONIBLE_PARA_EVALUACION("Disponible Para Evaluacion"),
    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada Con Observaciones"),
    NO_APROBADA("No Aprobada");

    private final String id;
    private final String nombre;

    EstadoFicha(String nombre) {
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
        return this == APROBADA || this == APROBADA_CON_OBSERVACIONES || this == NO_APROBADA;
    }

    public boolean permiteModificacion() {
        return !esTerminal();
    }

}
