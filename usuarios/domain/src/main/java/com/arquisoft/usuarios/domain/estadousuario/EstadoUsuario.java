package com.arquisoft.usuarios.domain.estadousuario;

import com.arquisoft.usuarios.domain.estadousuario.exception.EstadoUsuarioNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

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

    public static EstadoUsuario desde(String id) {
        return UtilEnum.desde(EstadoUsuario.class, id)
                .filter(estado -> estado != VACIO)
                .orElseThrow(() -> new EstadoUsuarioNoEncontradoException(id));
    }
}
