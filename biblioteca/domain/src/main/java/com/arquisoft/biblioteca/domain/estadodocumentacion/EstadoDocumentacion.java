package com.arquisoft.biblioteca.domain.estadodocumentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.exception.EstadoDocumentacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoDocumentacion {

    PENDIENTE("Pendiente"),
    PUBLICADO("Publicado"),
    NO_PUBLICADO("No Publicado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoDocumentacion(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoDocumentacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoDocumentacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoDocumentacion> delCatalogo(String id) {
        return UtilEnum.desde(EstadoDocumentacion.class, id).filter(estado -> estado != VACIO);
    }
}
