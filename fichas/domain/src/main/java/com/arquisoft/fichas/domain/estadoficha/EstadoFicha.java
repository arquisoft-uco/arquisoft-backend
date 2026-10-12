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
    DESCARTADA("Descartada"),

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

    public boolean esAprobatorio() {
        return this == APROBADA || this == APROBADA_CON_OBSERVACIONES;
    }

    public boolean permiteModificacion() {
        return !esTerminal();
    }

    public boolean esAsignablePorAsesor() {
        return this == EN_CONSTRUCCION || this == DISPONIBLE_PARA_EVALUACION || this == DESCARTADA;
    }

    public boolean permiteTransicionPorAsesorA(EstadoFicha destino) {
        return switch (this) {
            case EN_CONSTRUCCION -> destino == DISPONIBLE_PARA_EVALUACION || destino == DESCARTADA;
            case DISPONIBLE_PARA_EVALUACION -> destino == EN_CONSTRUCCION || destino == DESCARTADA;
            case DESCARTADA -> destino == EN_CONSTRUCCION;
            case APROBADA, APROBADA_CON_OBSERVACIONES, NO_APROBADA, VACIO -> false;
        };
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
