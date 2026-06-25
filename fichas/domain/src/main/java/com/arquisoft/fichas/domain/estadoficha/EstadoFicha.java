package com.arquisoft.fichas.domain.estadoficha;

public enum EstadoFicha {

    EN_CONSTRUCCION("En Construccion"),
    EN_REVISION("En Revision"),
    DISPONIBLE_PARA_EVALUACION("Disponible Para Evaluacion"),
    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada Con Observaciones"),
    NO_APROBADA("No Aprobada");

    private final String nombre;

    EstadoFicha(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoFicha desdeCatalogo(String nombre) {
        for (EstadoFicha ef : values()) {
            if (ef.nombre.equals(nombre)) {
                return ef;
            }
        }
        throw new IllegalArgumentException("Estado de ficha desconocido: " + nombre);
    }
}
