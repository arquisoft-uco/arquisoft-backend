package com.arquisoft.fichas.domain.estadoficha;

import com.arquisoft.fichas.domain.estadoficha.exception.EstadoFichaNoEncontradoException;
import com.arquisoft.shared.util.UtilTexto;

public enum EstadoFicha {

    EN_CONSTRUCCION("En Construccion"),
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

    public static EstadoFicha desde(String id) {
        if (UtilTexto.esVacioONulo(id)) {
            throw new EstadoFichaNoEncontradoException(id);
        }
        try {
            return valueOf(id);
        } catch (IllegalArgumentException ex) {
            throw new EstadoFichaNoEncontradoException(id);
        }
    }

}
