package com.arquisoft.fichas.domain.estadoficha;

import com.arquisoft.fichas.domain.estadoficha.exception.EstadoFichaNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoFicha {

    EN_CONSTRUCCION("En Construccion"),
    DISPONIBLE_PARA_EVALUACION("Disponible Para Evaluacion"),
    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada Con Observaciones"),
    NO_APROBADA("No Aprobada"),

    VACIO("");

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
        return delCatalogo(id).orElseThrow(() -> new EstadoFichaNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoFicha> delCatalogo(String id) {
        return UtilEnum.desde(EstadoFicha.class, id).filter(estado -> estado != VACIO);
    }

}
