package com.arquisoft.artefactos.domain.estudiante;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class EstudianteDomain {

    public static final EstudianteDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO);

    private UUID id;
    private String nombre;
    private String email;

    private EstudianteDomain() {}

    public static EstudianteDomain reconstruir(UUID id, String nombre, String email) {
        var replica = new EstudianteDomain();
        replica.id = id;
        replica.nombre = nombre;
        replica.email = email;
        return replica;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
