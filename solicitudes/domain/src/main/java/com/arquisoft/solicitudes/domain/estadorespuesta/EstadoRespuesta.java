package com.arquisoft.solicitudes.domain.estadorespuesta;

import com.arquisoft.solicitudes.domain.estadorespuesta.exception.EstadoRespuestaNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoRespuesta {

    APROBADA("Aprobada"),
    NO_APROBADA("No aprobada"),
    EN_REVISION("En revisión"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoRespuesta(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoRespuesta desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoRespuestaNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoRespuesta> delCatalogo(String id) {
        return UtilEnum.desde(EstadoRespuesta.class, id).filter(estado -> estado != VACIO);
    }
}
