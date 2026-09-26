package com.arquisoft.usuarios.domain.estadousuario;

import com.arquisoft.usuarios.domain.estadousuario.exception.EstadoUsuarioNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoUsuario {

    ACTIVO("Activo"),
    INACTIVO("Inactivo"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoUsuario(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean habilitaAcceso() {
        return this == ACTIVO;
    }

    public static EstadoUsuario desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoUsuarioNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoUsuario> delCatalogo(String id) {
        return UtilEnum.desde(EstadoUsuario.class, id).filter(estado -> estado != VACIO);
    }
}
